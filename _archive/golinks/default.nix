{ pkgs, repo, myLib, nix2container, version, ... }:

let
  inherit (repo) gitignoreSource;
  inherit (repo.lib) concatStringsSep;
  inherit (myLib)
    mkJar
    mkNativeFromJar
    ;


  deps = import ./deps.nix {
    inherit (pkgs) fetchMavenArtifact fetchgit lib;
  };

  depsPaths = deps.makePaths { };

  # NB: this is useful if static assets are used and need to be packaged inside
  # the jar.
  #resources = builtins.filterSource (_: type: type != "symlink") ./resources;
  classpath.prod = concatStringsSep ":" (
    (map gitignoreSource [ ./src ]) ++ depsPaths # [ resources ] ++
  );

  mainClass = "golinks.core";
  src = ./.;

  img-container = mkOCIClojureImage jar;
  container-script = myLib.mkOCIUploadScript img-container "j4m3s/golinks" version;

  mkOCIClojureImage = jar: nix2container.buildImage {
    name = "golinks";
    config.cmd = ["/bin/java" "-jar" jar];
    copyToRoot = [
      (pkgs.buildEnv {
        name = "root";
        paths = (builtins.attrValues {
          inherit (pkgs)
            bashInteractive
            coreutils

            jdk17
          ;
        });
        pathsToLink = [ "/bin" ];
      })
    ];
    layers = [
      (nix2container.buildLayer { deps = [jar]; })
    ];
  };

  jar = mkJar {
    name = "program.jar";
    inherit
      mainClass
      classpath
      src
    ;
  };
in
rec {
  inherit
    img-container
    jar
    container-script
  ;
  shell = myLib.mkShell {
    packages = with pkgs; [ graalvm17-ce ];
    shellHook = ''
      echo Test!
      echo Hi!
    '';
  };
}
