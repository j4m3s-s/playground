{ lib, pkgs, system, repo, self, nix2container, ... } @ inputs:

rec {
  inherit (builtins)
    filter
    attrValues
    attrNames
    concatMap
    typeOf
  ;
  ## Users
  # Function to create non mutable user with initial password
  mkUser = { keys ? [], initialHashedPassword, username }: {
    ${username} = {
      openssh.authorizedKeys.keys = keys;
      inherit initialHashedPassword;
    };
  };
  mkDefaultUsers = { initialHashedPassword, keys }: mkUser {
    username = "j4m3s";
    inherit
      initialHashedPassword
      keys
      ;
  } // mkUser {
    username = "root";
    inherit
      initialHashedPassword
      ;
  };

  ## Machine / Virtual Machine
  # This is an example of exposing a host derivation as a package.
  # Since technically everything is a derivation.
  mkNixosConfiguration = args: (import (pkgs.path + "/nixos/lib/eval-config.nix") args);
  mkHost = args: (mkNixosConfiguration args).config.system.build;
  mkHostVM = args: (mkHost args).vm;
  mkHostDerivation = args: (mkHost args).toplevel;

  # FIXME: Make this generic with pkgsImport function.
  mkHosts = specialArgs: path: builtins.listToAttrs (
    let
      hostsFolders = builtins.attrNames (builtins.readDir path);
    in
      map (folder:
        let
          hostPath = path + ("/" + folder);
        in
          {
            name = folder;
            value = (mkNixosConfiguration (mkHostArgs specialArgs hostPath));
          }) hostsFolders);


  mkHostArgs = specialArgs: path: {
    inherit system;
    specialArgs = specialArgs;
    modules = [
      path
    ];
  };



  # imports a path and all its file.nix as { file = pkg } and folder f having
  # default.nix as { f = pkg }
  pkgsImport = path: let
    # { toto.nix = "regular", tata = "folder"}
    entries = builtins.readDir path;
    # [toto.nix tata]
    entriesNames = builtins.attrNames entries;
  in
    # { toto, tata}
    builtins.listToAttrs (map (v: let
      file = (path + ( "/" + v));
    in {
      name = (lib.strings.removeSuffix ".nix" v);
      value = pkgs.callPackage file { inherit repo pkgs lib; };
    }) entriesNames);


  myCallPackage = path: args: pkgs.callPackage path (args // inputs);

  mkStaticHttpConfig = src: pkgs.writeTextFile {
    name = "lighttpd.conf";

    checkPhase = ''
    PORT=5000 ${pkgs.pkgsMusl.lighttpd}/bin/lighttpd -tt -f $out
  '';

    # FIXME:
    # - compression
    # - cache files
    # - Etag
    # - http2?
    text = ''
      server.document-root = "${src}"
      index-file.names = ( "index.html" )
      server.port = env.PORT
      include "${pkgs.pkgsMusl.lighttpd}/share/lighttpd/doc/config/conf.d/mime.conf"
    '';
  };
  mkStaticHttpCmd = src: [ "${pkgs.pkgsMusl.lighttpd}/bin/lighttpd" "-D" "-f" (mkStaticHttpConfig src)];

  mkDefaultHugoBlog = src: pkgs.stdenv.mkDerivation {
    pname = "hugo-blog";
    version = "dummy";

    buildInputs = [ pkgs.hugo ];

    inherit src;

    buildPhase = ''
    mkdir -p themes/
    ln -sf ${repo.third_party.papermod} themes/PaperMod

    hugo \
      --config=config.yaml \
      --cleanDestinationDir \
      --forceSyncStatic \
      --ignoreCache \
      --minify
  '';

    installPhase = ''
      cp -r public $out
  '';
  };

  mkOCIHugoBlog = blog: nix2container.buildImage {
    name = blog.name;
    config.cmd = repo.myLib.mkStaticHttpCmd blog;
    layers = [
      (nix2container.buildLayer { deps = [blog]; })
    ];
  };

  mkOCIUploadScript = container: containerName: containerVersion: let
  in pkgs.writeScript "upload" ''
    ${repo.third_party.skopeo-nix2container}/bin/skopeo copy nix:${container} docker-daemon:${containerName}:${containerVersion}
    echo Uploading ${containerName}:${containerVersion} ...
    ${pkgs.docker}/bin/docker push ${containerName}:${containerVersion}
  '';

  # Clojure build functions
  ## We need the uberjar alias to create this dependencies (as the presence of it
  ## inside generated deps.nix). Deps.nix is generated with clj2nix w/ uberjar
  ## alias.
  mkJar = {
    name,
    mainClass,
    classpath,
    src
  }: pkgs.stdenv.mkDerivation rec {
    inherit name;
    dontUnpack = true;
    buildPhase = ''
      export HOME=$(pwd)
      cp -rf ${src}/* .
      ${pkgs.clojure}/bin/clojure \
        -Scp ${classpath.prod} \
        -M:uberjar \
        ${name} \
        -C -m ${mainClass}
    '';

    doCheck = true;

    checkPhase = ''
      echo "checking for existence of ${name}"
      [ -f ${name} ]
    '';

    installPhase = ''
      cp ${name} $out
    '';

    # Enable CI target
    meta.ci.build = true;
  };

  ## Create native binary w/ GraalVM. Note that this is expensive CPU-wise and
  ## quite slow if you build it everytime. Startup latency of those binaries is
  ## lower though.
  ## Enterprise GraalVM has more features but it's currently
  mkNativeFromJar = {
    name,
    entryJar,
    reflectionConfig,
    classpath,
    doCheck ? true,

  }: pkgs.stdenv.mkDerivation rec {
    inherit name;

    dontUnpack = true;
    # ReportExceptionStackTraces : to get an idea of where reflection is used
    # no-fallback : don't build a slow image is reflection is used
    # Reflection configuration contains class to load that use reflection
    buildPhase = ''
      ${pkgs.graalvm17-ce}/bin/native-image \
      -cp ${classpath.prod} \
      -jar ${entryJar} \
      ${name} \
      --initialize-at-build-time \
      -H:+ReportExceptionStackTraces \
      -H:ReflectionConfigurationFiles=${reflectionConfig} \
      --no-fallback
    '';

    inherit doCheck;
    checkPhase = ''
      echo "checking for existence of ${name}"
      [ -f ${name} ]
      ./${name}
    '';

    installPhase = ''
      cp ${name} $out
    '';

    # Enable CI target
    meta.ci.build = true;
  };

  # Small wrapper that adds it build to build target by default
  mkShell = args: pkgs.mkShell (args // { meta.ci.build = true; });

  # dfs of all build targets, returns a list of them
  dfsAttr = eligible: attr:
    if typeOf attr == "set" then
      # Include the attr itself it it is eligible
      (if eligible attr then [ attr ] else [  ])
      # Include values of current attr if they're eligible
      ++ filter (val: eligible val) (attrValues attr)
      # Recurse into childrens
      ++ (if eligible attr then [ ]
          else concatMap (dfsAttr eligible) (map (attrName: attr."${attrName}") (attrNames attr)))
    else [  ]
  ;

  getBuildTargets = initialAttr: (dfsAttr (attr: (attr ? type && attr.type == "derivation") && (attr.meta.ci.build or false)) initialAttr);
}
