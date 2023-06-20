{ pkgs, repo, myLib, nix2container, version, ... }:

let
  inherit (myLib)
    mkJar
    mkNativeFromJar
  ;

  inherit (pkgs.lib)
    concatStringsSep
    importFile
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

  ytMusicCliPkgs = with pkgs; [
    yt-dlp
    chromaprint # for fpcalc fingerprint calculation
    id3v2
  ];

  mkOCIClojureImage = jar: nix2container.buildImage {
    name = "yt-music";
    config.cmd = ["/bin/java" "-jar" jar];
    copyToRoot = [
      (pkgs.buildEnv {
        name = "root";
        paths = (builtins.attrValues {
          inherit (pkgs)
            bashInteractive
            coreutils

            jdk11
          ;
        }) ++ ytMusicCliPkgs;
        pathsToLink = [ "/bin" ];
      })
    ];
    layers = [
      (nix2container.buildLayer { deps = [jar]; })
    ];
  };

  #img-container = mkOCIClojureImage jar;
  #container-script = myLib.mkOCIUploadScript img-container "j4m3s/yt-music" version;

  src = ./.;
  mainClass = "yt-music.core";

  jar = (mkJar {
    name = "yt-music.jar";
    inherit
      mainClass
      classpath
      src
    ;
  });
in
{
  shell = myLib.mkShell {
    buildInputs = ytMusicCliPkgs;
  };
  inherit
    # FIXME: build currently broken
    #img-container
    #container-script
    #bin
    jar
  ;
}
