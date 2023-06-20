{ pkgs, repo, myLib, ... }:

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
    (map gitignoreSource [ ./src ./test ]) ++ depsPaths # [ resources ] ++
  );

  mainClass = "jms.core";
  src = ./.;
in rec {
  webhooktest-jar = mkJar {
    name = "webhook-jms.jar";
    mainClass = "jms.webhooktest";
    inherit
      src
      classpath
    ;
  };
  jar = mkJar {
    name = "jms.jar";
    inherit
      mainClass
      classpath
      src
    ;
  };
  bin = mkNativeFromJar {
    name = "jms";
    entryJar = jar;
    reflectionConfig = ./reflect-cfg.json;
    inherit
      classpath
    ;
  };
  shell = pkgs.mkShell {
    packages = with pkgs; [ hello ];
    shellHook = ''
      echo Test!
      echo Hi!
    '';
  };
}
