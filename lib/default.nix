{ lib, pkgs, system, repo, self, nix2container, ... } @ inputs:

rec {
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
    version = "${builtins.substring 0 8 (self.lastModifiedDate or self.lastModified or "19700101")}_${self.shortRev or "dirty"}";

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

  mkOCIHugoBlogUploadScript = blog: containerName: containerVersion: let
    container = mkOCIHugoBlog blog;
  in pkgs.writeScript "upload" ''
    echo Uploading ${containerName}:${containerVersion} ...
    ${repo.third_party.skopeo-nix2container}/bin/skopeo copy nix:${container} docker-daemon:${containerName}:${containerVersion}
  '';
}
