{ pkgs, ... }:

let
  deps = import ./deps.nix {
    inherit (pkgs) fetchMavenArtifact fetchgit lib;
  };

  mkJar = name: opts:
  with pkgs;
  #assert (hasSuffix ".jar" name);
  stdenv.mkDerivation rec {
    inherit name;
    dontUnpack = true;
    buildPhase = ''
      export HOME=$(pwd)
      cp ${./pom.xml} pom.xml
      cp ${./deps.edn} deps.edn
      ${clojure}/bin/clojure \
      -Scp ${classpath.prod} \
      -A:uberjar \
      ${name} \
      -C ${opts}
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
in mkJar "jms.jar" "-m jms.core"
