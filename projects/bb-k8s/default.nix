{ pkgs, repo, myLib, ... }:

let
  inherit (repo) gitignoreSource;
  inherit (repo.lib) concatStringsSep;
  inherit (myLib)
    mkNativeFromJar
    mkJar
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

  mainClass = "b10s.main";
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
  #bin = mkNativeFromJar {
  #  name = "bin";
  #  entryJar = jar;
  #  reflectionConfig = ./reflect-cfg.json;
  #  # We need runtime, hence deactivating no-fallback.
  #  noFallback = false;
  #  inherit
  #    classpath
  #  ;
  #};
  shell = myLib.mkShell {
    packages = with pkgs; [
      graalvm17-ce
      zlib.dev
      bb
    ];
    shellHook = ''
      echo Test!
      echo Hi!
    '';
  };
}
