# generated by clj2nix-1.1.0-rc
{ fetchMavenArtifact, fetchgit, lib }:

let repos = [
        "https://repo1.maven.org/maven2/"
        "https://repo.clojars.org/" ];

  in rec {
      makePaths = {extraClasspaths ? []}:
        if (builtins.typeOf extraClasspaths != "list")
        then builtins.throw "extraClasspaths must be of type 'list'!"
        else (lib.concatMap (dep:
          builtins.map (path:
            if builtins.isString path then
              path
            else if builtins.hasAttr "jar" path then
              path.jar
            else if builtins.hasAttr "outPath" path then
              path.outPath
            else
              path
            )
          dep.paths)
        packages) ++ extraClasspaths;
      makeClasspaths = {extraClasspaths ? []}:
       if (builtins.typeOf extraClasspaths != "list")
       then builtins.throw "extraClasspaths must be of type 'list'!"
       else builtins.concatStringsSep ":" (makePaths {inherit extraClasspaths;});
      packageSources = builtins.map (dep: dep.src) packages;
      packages = [
  rec {
    name = "clojure/org.clojure";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "clojure";
      groupId = "org.clojure";
      sha512 = "4bb567b9262d998f554f44e677a8628b96e919bc8bcfb28ab2e80d9810f8adf8f13a8898142425d92f3515e58c57b16782cff12ba1b5ffb38b7d0ccd13d99bbc";
      version = "1.10.3";

    };
    paths = [ src ];
  }

  rec {
    name = "tools.analyzer/org.clojure";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "tools.analyzer";
      groupId = "org.clojure";
      sha512 = "c51752a714848247b05c6f98b54276b4fe8fd44b3d970070b0f30cd755ac6656030fd8943a1ffd08279af8eeff160365be47791e48f05ac9cc2488b6e2dfe504";
      version = "1.1.0";

    };
    paths = [ src ];
  }

  rec {
    name = "core.specs.alpha/org.clojure";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "core.specs.alpha";
      groupId = "org.clojure";
      sha512 = "c1d2a740963896d97cd6b9a8c3dcdcc84459ea66b44170c05b8923e5fbb731b4b292b217ed3447bbc9e744c9a496552f77a6c38aea232e5e69f8faa627dea4b5";
      version = "0.2.56";

    };
    paths = [ src ];
  }

  rec {
    name = "spec.alpha/org.clojure";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "spec.alpha";
      groupId = "org.clojure";
      sha512 = "0740dc3a755530f52e32d27139a9ebfd7cbdb8d4351c820de8d510fe2d52a98acd6e4dfc004566ede3d426e52ec98accdca1156965218f269e60dd1cd4242a73";
      version = "0.2.194";

    };
    paths = [ src ];
  }

  rec {
    name = "tools.analyzer.jvm/org.clojure";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "tools.analyzer.jvm";
      groupId = "org.clojure";
      sha512 = "36ad50a7a79c47dea16032fc4b927bd7b56b8bedcbd20cc9c1b9c85edede3a455369b8806509b56a48457dcd32e1f708f74228bce2b4492bd6ff6fc4f1219d56";
      version = "1.2.2";

    };
    paths = [ src ];
  }

  rec {
    name = "depstar/seancorfield";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "depstar";
      groupId = "seancorfield";
      sha512 = "0f4458b39b8b1949755bc2fe64b239673a9efa3a0140998464bbbcab216ec847344c1b8920611f7c9ca07261850f3a08144ae221cc2c41813a080189e32f9c10";
      version = "1.0.94";

    };
    paths = [ src ];
  }

  rec {
    name = "asm/org.ow2.asm";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "asm";
      groupId = "org.ow2.asm";
      sha512 = "876eac7406e60ab8b9bd6cd3c221960eaa53febea176a88ae02f4fa92dbcfe80a3c764ba390d96b909c87269a30a69b1ee037a4c642c2f535df4ea2e0dd499f2";
      version = "9.2";

    };
    paths = [ src ];
  }

  rec {
    name = "tools.reader/org.clojure";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "tools.reader";
      groupId = "org.clojure";
      sha512 = "3481259c7a1eac719db2921e60173686726a0c2b65879d51a64d516a37f6120db8ffbb74b8bd273404285d7b25143ab5c7ced37e7c0eaf4ab1e44586ccd3c651";
      version = "1.3.6";

    };
    paths = [ src ];
  }

  rec {
    name = "core.memoize/org.clojure";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "core.memoize";
      groupId = "org.clojure";
      sha512 = "67196537084b7cc34a01454d2a3b72de3fddce081b72d7a6dc1592d269a6c2728b79630bd2d52c1bf2d2f903c12add6f23df954c02ef8237f240d7394ccc3dde";
      version = "1.0.253";

    };
    paths = [ src ];
  }

  rec {
    name = "data.priority-map/org.clojure";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "data.priority-map";
      groupId = "org.clojure";
      sha512 = "bb8bc5dbfd3738c36b99a51880ac3f1381d6564e67601549ef5e7ae2b900e53cdcdfb8d0fa4bf32fb8ebc4de89d954bfa3ab7e8a1122bc34ee5073c7c707ac13";
      version = "1.1.0";

    };
    paths = [ src ];
  }

  rec {
    name = "core.cache/org.clojure";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "core.cache";
      groupId = "org.clojure";
      sha512 = "0a07ceffc2fa3a536b23773eefc7ef5e1108913b93c3a5416116a6566de76dd5c218f3fb0cc19415cbaa8843838de310b76282f20bf1fc3467006c9ec373667e";
      version = "1.0.225";

    };
    paths = [ src ];
  }

  rec {
    name = "core.async/org.clojure";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "core.async";
      groupId = "org.clojure";
      sha512 = "6c80a6ff6fe7ec8503c36a97684e4118ee1b103983b68c8ce21a398661ede02255e4b04a16fbabd112c8d57b7dd28967f6708e8d3461a5a393e019cda7ca4e96";
      version = "1.6.673";

    };
    paths = [ src ];
  }

  ];
  }
  