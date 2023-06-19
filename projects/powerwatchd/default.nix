{ pkgs, repo, ... }:

with pkgs.lib;

let
  gitignoreSource = repo.gitignoreSource;
  deps = import ./deps.nix {
    inherit (pkgs) fetchMavenArtifact fetchgit lib;
  };

  depsPaths = deps.makePaths { };

  #resources = builtins.filterSource (_: type: type != "symlink") ./resources;

  classpath.prod = concatStringsSep ":" (
    (map gitignoreSource [ ./src ./test ]) ++ depsPaths # [ resources ] ++
  );

  mkJar = {
    name,
    mainClass
  }: pkgs.stdenv.mkDerivation rec {
    inherit name;
    dontUnpack = true;
    buildPhase = ''
      export HOME=$(pwd)
      cp -rf ${./.}/* .
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
  };

  mkNativeFromJar = {
    name,
    entryJar,
    reflectionConfig
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

    doCheck = true;
    checkPhase = ''
      echo "checking for existence of ${name}"
      [ -f ${name} ]
      ./${name}
    '';

    installPhase = ''
      cp ${name} $out
    '';
  };

  mainClass = "powerwatchd.core";
in
rec {
  jar = mkJar {
    name = "program.jar";
    inherit mainClass;
  };
  bin = mkNativeFromJar {
    name = "bin";
    entryJar = jar;
    reflectionConfig = ./reflect-cfg.json;
  };
  shell = pkgs.mkShell {
    packages = with pkgs; [ hello ];
    shellHook = ''
      echo Test!
      echo Hi!
    '';
  };
}
