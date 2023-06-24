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

  mainClass = "powerwatchd.core";
  src = ./.;
in
rec {
  jar = mkJar {
    name = "program.jar";
    inherit
      mainClass
      classpath
      src
    ;
  };
  bin = mkNativeFromJar {
    name = "bin";
    entryJar = jar;
    reflectionConfig = ./reflect-cfg.json;
    inherit
      classpath
    ;
    # We don't check existence since this is a server and will run for ever
    doCheck = false;
  };
  shell = myLib.mkShell {
    packages = with pkgs; [ hello ];
    shellHook = ''
      echo Test!
      echo Hi!
    '';
  };
}
