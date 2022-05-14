{ pkgs, gitignoreSource, ... }:

with pkgs.lib;

let
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
        # cambada edits pom.xml
        chmod +w pom.xml
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
in
mkJar "jms.jar" "jms.core"
