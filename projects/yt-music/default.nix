{ pkgs, repo, myLib, nix2container, version, ... }:

with pkgs.lib;

let
  stdenv = pkgs.stdenv;

  gitignoreSource = repo.gitignoreSource;
  deps = import ./deps.nix {
    inherit (pkgs) fetchMavenArtifact fetchgit lib;
  };

  depsPaths = deps.makePaths { };

  #resources = builtins.filterSource (_: type: type != "symlink") ./resources;

  classpath.prod = concatStringsSep ":" (
    (map gitignoreSource [ ./src ./test ]) ++ depsPaths # [ resources ] ++
  );

  projectSrc = builtins.filterSource (path: _type: path != "default.nix") ./.;

  mkJar = name: mainClass:
    with pkgs;
    #assert (hasSuffix ".jar" name);
    stdenv.mkDerivation rec {
      inherit name;
      dontUnpack = true;
      buildPhase = ''
        export HOME=$(pwd)
        cp -rf ${projectSrc}/* .
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
  inherit (pkgs) hello;
  jar = mkJar "yt-music.jar" "yt-music.core";
  bin = mkNativeFromJar "yt-music" jar ./reflect-cfg.json;
  img-container = nix2container.buildImage {
      name = "yt-music";
      #config.cmd = ["${pkgs.jdk11}/bin/java -jar ${jar}"];
      config.cmd = ["/bin/java" "-jar" jar];
      copyToRoot = [
        (pkgs.buildEnv {
          name = "root";
          paths = [ pkgs.bashInteractive pkgs.coreutils pkgs.jdk11 ];
          pathsToLink = [ "/bin" ];
        })
      ];
      layers = [
        (nix2container.buildLayer { deps = [jar]; })

      ];
    };
  container-script = myLib.mkOCIUploadScript img-container "j4m3s/yt-music" version;
}
