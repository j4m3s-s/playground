{ pkgs, repo, myLib, nix2container, version, ... }:

with pkgs.lib;

let
  inherit (myLib)
    mkJar
    mkNativeFromJar
  ;

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

  ytMusicCliPkgs = with pkgs; [
    yt-dlp
    chromaprint # for fpcalc fingerprint calculation
    id3v2
  ];

  img-container = nix2container.buildImage {
    name = "yt-music";
    #config.cmd = ["${pkgs.jdk11}/bin/java -jar ${jar}"];
    config.cmd = ["/bin/java" "-jar" jar];
    copyToRoot = [
      (pkgs.buildEnv {
        name = "root";
        paths = with pkgs; [
          bashInteractive
          coreutils

          jdk11
        ] ++ ytMusicCliPkgs;
        pathsToLink = [ "/bin" ];
      })
    ];
    layers = [
      (nix2container.buildLayer { deps = [jar]; })
    ];
  };
  container-script = myLib.mkOCIUploadScript img-container "j4m3s/yt-music" version;
  src = ./.;
  mainClass = "yt-music.core";
  jar = mkJar {
    name = "yt-music.jar";
    inherit
      mainClass
      classpath
      src
    ;
    # Currently build is broken
    meta.ci.build = false;
  };
in
{
  bin = mkNativeFromJar {
    name = "bin";
    entryJar = jar;
    reflectionConfig = ./reflect-cfg.json;
    inherit
      classpath
    ;
    # Currently jar build is broken
    meta.ci.build = false;
  };
  shell = myLib.mkShell {
    buildInputs = ytMusicCliPkgs;
    meta.ci.build = true;
  };
  #bin = mkNativeFromJar "yt-music" jar ./reflect-cfg.json;
  inherit
    #container-script
    #img-container
    jar
  ;
}
