{ pkgs, stdenv, repo, ... }:

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

  mkJar = name: mainClass:
    with pkgs;
    #assert (hasSuffix ".jar" name);
    stdenv.mkDerivation rec {
      inherit name;
      dontUnpack = true;
      buildPhase = ''
        export HOME=$(pwd)
        cp -rf ${./.}/* .
        ${clojure}/bin/clojure \
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

  mkNativeFromJar = name: entryJar: reflectionConfig:
    stdenv.mkDerivation rec {
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
in
rec {
  jms-jar = mkJar "jms.jar" "jms.core";
  webhooktest-jar = mkJar "jms.jar" "jms.webhooktest";
  jms = mkNativeFromJar "jms" jms-jar ./reflect-cfg.json;
}
