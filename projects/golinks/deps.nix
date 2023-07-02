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
    name = "transit-java/com.cognitect";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "transit-java";
      groupId = "com.cognitect";
      sha512 = "a97c4bb0c10cf9f024b846d528f24b04fb0c0469e06f3ba9f7b0906d490b9022aa8c6b1b00bfd4c7cf50ac3e53c9836b42ac744119af186b848c5700e45fcac6";
      version = "0.8.337";

    };
    paths = [ src ];
  }

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
    name = "commons-codec/commons-codec";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "commons-codec";
      groupId = "commons-codec";
      sha512 = "da30a716770795fce390e4dd340a8b728f220c6572383ffef55bd5839655d5611fcc06128b2144f6cdcb36f53072a12ec80b04afee787665e7ad0b6e888a6787";
      version = "1.15";

    };
    paths = [ src ];
  }

  rec {
    name = "tools.analyzer/org.clojure";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "tools.analyzer";
      groupId = "org.clojure";
      sha512 = "9cce94540a6fd0ae0bad915efe9a30c8fb282fbd1e225c4a5a583273e84789b3b5fc605b06f11e19d7dcc212d08bc6138477accfcde5d48839bec97daa874ce6";
      version = "0.6.9";

    };
    paths = [ src ];
  }

  rec {
    name = "jetty-servlet/org.eclipse.jetty";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "jetty-servlet";
      groupId = "org.eclipse.jetty";
      sha512 = "72e5e7b242614029d044dfb6ba40fd22725dd9e731547d11cc170d2346414e7840dcb40803e122892e909cc5435fba94e84d792abdf4648cf06c7b9dedd9a80c";
      version = "9.4.44.v20210927";

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
    name = "pedestal.jetty/io.pedestal";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "pedestal.jetty";
      groupId = "io.pedestal";
      sha512 = "eea4f6d60fc45bfa87949a8e447f358d1334f6cb49a3fba0415d13fddbc65448bd4a1b86e34462543f1f19cc6bf0e0450cd9f9dc1c60ddcd73f9be479f9707db";
      version = "0.5.10";

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
    name = "http2-server/org.eclipse.jetty.http2";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "http2-server";
      groupId = "org.eclipse.jetty.http2";
      sha512 = "0874dfbb36ec2484b9f497d6d4eb5336b41d3e0551dade945b4b5c24243b27d9137cdc1af4ed24ae0f8aafb6fb106db356646a2d999b28961be7f033b257f3a2";
      version = "9.4.44.v20210927";

    };
    paths = [ src ];
  }

  rec {
    name = "commons-fileupload/commons-fileupload";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "commons-fileupload";
      groupId = "commons-fileupload";
      sha512 = "a8780b7dd7ab68f9e1df38e77a5207c45ff50ec53d8b1476570d069edc8f59e52fb1d0fc534d7e513ac5a01b385ba73c320794c82369a72bd6d817a3b3b21f39";
      version = "1.4";

    };
    paths = [ src ];
  }

  rec {
    name = "jetty-http/org.eclipse.jetty";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "jetty-http";
      groupId = "org.eclipse.jetty";
      sha512 = "712ada0e464a88366285260824846262ed720d777fb0f83aa34942b0ec9583a2ca8dbc393bc2acd59f5441a60f2de5d3c0cad16658983bf31ef058810dbf8427";
      version = "9.4.44.v20210927";

    };
    paths = [ src ];
  }

  rec {
    name = "jetty-util/org.eclipse.jetty";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "jetty-util";
      groupId = "org.eclipse.jetty";
      sha512 = "7a5ad20662da63fc19c8cf0bd65c94dc00ac80625d8538619cf3ad9c7358a0225a933cd1ca408017a1869267938b3cdfd30f075fc3364ed6cf384d19b3c0ac8d";
      version = "9.4.44.v20210927";

    };
    paths = [ src ];
  }

  rec {
    name = "tools.analyzer.jvm/org.clojure";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "tools.analyzer.jvm";
      groupId = "org.clojure";
      sha512 = "ec1cb7638e38dfdca49c88e0b71ecf9c6ea858dccd46a2044bb37d01912ab4709b838cd2f0d1c2f201927ba4eea8f68d4d82e9fdd6da2f9943f7239bf86549f2";
      version = "0.7.2";

    };
    paths = [ src ];
  }

  rec {
    name = "pedestal.log/io.pedestal";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "pedestal.log";
      groupId = "io.pedestal";
      sha512 = "229c6c73dc7ec39aee15f54ce81cf2b33301e444241f2dff2066b4c10ff47968f3caa95a8a918601e8f3e7563b34da6925e533615e7ce8b1f39bdb97ca082757";
      version = "0.5.10";

    };
    paths = [ src ];
  }

  rec {
    name = "jackson-dataformat-cbor/com.fasterxml.jackson.dataformat";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "jackson-dataformat-cbor";
      groupId = "com.fasterxml.jackson.dataformat";
      sha512 = "82695d50cd7df578388db52433f56c8533e5565d01d2c17cc18b26536367eedd7a249afe070249fdaf8fa02c3d1c06a542d0a4303696b4d1ddc4cdcb3145a7bc";
      version = "2.9.9";

    };
    paths = [ src ];
  }

  rec {
    name = "pedestal.service/io.pedestal";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "pedestal.service";
      groupId = "io.pedestal";
      sha512 = "c917ad911d7705a8b08784f1342bfa6180a4e8b3cec210878bb4ba24cf2a497da344573872123c09f8fe13a3189ca324bdc510b10c140fd047169783094c7e2b";
      version = "0.5.10";

    };
    paths = [ src ];
  }

  rec {
    name = "json-simple/com.googlecode.json-simple";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "json-simple";
      groupId = "com.googlecode.json-simple";
      sha512 = "f8798bfbcc8ab8001baf90ce47ec2264234dc1da2d4aa97fdcdc0990472a6b5a5a32f828e776140777d598a99d8a0c0f51c6d0767ae1a829690ab9200ae35742";
      version = "1.1.1";

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
    name = "websocket-api/org.eclipse.jetty.websocket";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "websocket-api";
      groupId = "org.eclipse.jetty.websocket";
      sha512 = "3a81902c541d37084cf511aac53b780a937d02269837bff0eb68443d8d4ba9bb533d4f8bb24315b8d460e7396b3e257a71d40b0c4f97eb49defc55693adf1100";
      version = "9.4.44.v20210927";

    };
    paths = [ src ];
  }

  rec {
    name = "alpn-api/org.eclipse.jetty.alpn";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "alpn-api";
      groupId = "org.eclipse.jetty.alpn";
      sha512 = "b9570b3323337dcdc192e640288633743736ef9206adc4cda88db7da77df49732bba0a4e85613225ffec32ac72c415a84fcd2353c04f8708dad85142a2b439f8";
      version = "1.1.3.v20160715";

    };
    paths = [ src ];
  }

  rec {
    name = "commons-io/commons-io";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "commons-io";
      groupId = "commons-io";
      sha512 = "6af22dffaaecd1553147e788b5cf50368582318f396e456fe9ff33f5175836713a5d700e51720465c932c2b1987daa83027358005812d6a95d5755432de3a79d";
      version = "2.10.0";

    };
    paths = [ src ];
  }

  rec {
    name = "http2-hpack/org.eclipse.jetty.http2";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "http2-hpack";
      groupId = "org.eclipse.jetty.http2";
      sha512 = "7e6d558cadb52ed8d25bf0bbd0e33a30b7cf48581595a50605499874ad423ba82c162f2e1c0d5665f26f2351e667bf5180a4bb5b4ca4ae2a24ca2f2c9d88e032";
      version = "9.4.44.v20210927";

    };
    paths = [ src ];
  }

  rec {
    name = "websocket-client/org.eclipse.jetty.websocket";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "websocket-client";
      groupId = "org.eclipse.jetty.websocket";
      sha512 = "a85e66f1800440718349d373c9e533b95bfa7780511e6b9fa8444bee0e70fa6a2559621266195fa95e03979d98cd81defab95d3508e532f0d584ef8039931589";
      version = "9.4.44.v20210927";

    };
    paths = [ src ];
  }

  rec {
    name = "jackson-core/com.fasterxml.jackson.core";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "jackson-core";
      groupId = "com.fasterxml.jackson.core";
      sha512 = "d8beac9e71444bc795c9d99308ead3284a39aa161f825708da7dbdfce410d099c0bbc76c31c27adad540cf3bccf6826d539fcb157923efae84b10b3778b920a9";
      version = "2.9.9";

    };
    paths = [ src ];
  }

  rec {
    name = "websocket-servlet/org.eclipse.jetty.websocket";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "websocket-servlet";
      groupId = "org.eclipse.jetty.websocket";
      sha512 = "c73553c8c784998f9e9c4de7c37403cbd60bfbc80ae2bfe2eb4ef55b255d032ec32957d2c745be1be899bbeeae69744ade7b4da62178c598a79556b4b78fdc75";
      version = "9.4.44.v20210927";

    };
    paths = [ src ];
  }

  rec {
    name = "jetty-security/org.eclipse.jetty";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "jetty-security";
      groupId = "org.eclipse.jetty";
      sha512 = "6f816f12e37e8242922f879b497f3f5642c8ece725f1b874e05dc5c30e396baca2908e3f832d040ca04f5f121c09cd3e05637082b6b8a3ea5949267cfe8c3d61";
      version = "9.4.44.v20210927";

    };
    paths = [ src ];
  }

  rec {
    name = "slf4j-simple/org.slf4j";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "slf4j-simple";
      groupId = "org.slf4j";
      sha512 = "cdcebe8fa58527a1bc7da0c18e90a9547ce8ac99cccfe5657860c2a25478c030ea758251da3e32a71eab9cbb91360692b5c6c5887a1f1597d1fda07151b27e5f";
      version = "1.7.36";

    };
    paths = [ src ];
  }

  rec {
    name = "pedestal.interceptor/io.pedestal";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "pedestal.interceptor";
      groupId = "io.pedestal";
      sha512 = "b6d99300e2c656fc63841449067d74893c63f884d282ff1af40510e5e53baac0f17ac869212e0023dc01d3766248ce28e93e32a57239926254e3af0240c82178";
      version = "0.5.10";

    };
    paths = [ src ];
  }

  rec {
    name = "metrics-core/io.dropwizard.metrics";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "metrics-core";
      groupId = "io.dropwizard.metrics";
      sha512 = "4b500efcc88e717dbbfff9629e12db0f23380bc7dbae820039ed730cdaf26fb6d5be6e58434bd6f688ea3d675576e2057ec183472aac99189817fc28b3c3489e";
      version = "4.1.0";

    };
    paths = [ src ];
  }

  rec {
    name = "jetty-util-ajax/org.eclipse.jetty";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "jetty-util-ajax";
      groupId = "org.eclipse.jetty";
      sha512 = "6c496929f13d51d03b8915c21853c629270ee4fa7263fa6b10244110bf0ef7f0724cff069ba19a165e4e2a03ec549a057a5a8e7de2341944a38c94a3e5fc4bd2";
      version = "9.4.44.v20210927";

    };
    paths = [ src ];
  }

  rec {
    name = "hiccup/hiccup";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "hiccup";
      groupId = "hiccup";
      sha512 = "034f15be46c35029f41869c912f82cb2929fbbb0524ea64bd98dcdb9cf09875b28c75e926fa5fff53942b0f9e543e85a73a2d03c3f2112eecae30fcef8b148f4";
      version = "1.0.5";

    };
    paths = [ src ];
  }

  rec {
    name = "crouton/crouton";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "crouton";
      groupId = "crouton";
      sha512 = "301ba56e79f84814db73884085d5a30c75e50b045fee227c7ccacdc7df892edbd689a5615cac5c424433b106174139d4362970c93019a6c333b9ad868d34a545";
      version = "0.1.2";

    };
    paths = [ src ];
  }

  rec {
    name = "opentracing-api/io.opentracing";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "opentracing-api";
      groupId = "io.opentracing";
      sha512 = "931197ca33e509570e389cd163af96e277bb3635f019e34e2fc97d3fa9c34bb9042f25b2ba8aa59f8516cc044ec3e9584462601b8aa5f954bbc6ad88e5fbe5cd";
      version = "0.33.0";

    };
    paths = [ src ];
  }

  rec {
    name = "javassist/org.javassist";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "javassist";
      groupId = "org.javassist";
      sha512 = "ad65ee383ed83bedecc2073118cb3780b68b18d5fb79a1b2b665ff8529df02446ad11e68f9faaf4f2e980065f5946761a59ada379312cbb22d002625abed6a4f";
      version = "3.18.1-GA";

    };
    paths = [ src ];
  }

  rec {
    name = "environ/environ";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "environ";
      groupId = "environ";
      sha512 = "e318da117facf51a27b9ae39279363c4d824393215ce63fa69e5bdb65a910d1f8632d712598ad0c3f4248d0ab9274a97161859393d265d74d50ad2a4cdbddf41";
      version = "1.2.0";

    };
    paths = [ src ];
  }

  rec {
    name = "http2-common/org.eclipse.jetty.http2";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "http2-common";
      groupId = "org.eclipse.jetty.http2";
      sha512 = "ed5d09b4727f78fcd5c144b5580c2a1087afad328818ba9febdd46380985a8e57f73835aa89ea6564f00a6663d705d29960e4d6cb19d8bb403d814634dc2175e";
      version = "9.4.44.v20210927";

    };
    paths = [ src ];
  }

  rec {
    name = "msgpack/org.msgpack";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "msgpack";
      groupId = "org.msgpack";
      sha512 = "a2741bed01f9c37ba3dbe6a7ab9ce936d36d4da97c35e215250ac89ac0851fc5948d83975ea6257d5dce1d43b6b5147254ecfb4b33f9bbdc489500b3ff060449";
      version = "0.6.12";

    };
    paths = [ src ];
  }

  rec {
    name = "transit-clj/com.cognitect";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "transit-clj";
      groupId = "com.cognitect";
      sha512 = "d13f18d898195d69209675877ff197472e051dd6b9ba7b127f4d5b0e2f24b7c60cdddddd7a4a86f1fc78717ddc5c95bc88c24044dcb571a1c234d1250f38e67c";
      version = "0.8.313";

    };
    paths = [ src ];
  }

  rec {
    name = "opentracing-noop/io.opentracing";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "opentracing-noop";
      groupId = "io.opentracing";
      sha512 = "c727bcf20504fa72bfc07456bdde3b0b50988632d85c7af78df742efd90a431c125f5d644273203fa211a62fc4a282455cf281c7c82b82df4695afbc5488577f";
      version = "0.33.0";

    };
    paths = [ src ];
  }

  rec {
    name = "enduro/alandipert";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "enduro";
      groupId = "alandipert";
      sha512 = "9d71079e2c173b5518e5687efe01624e6bfa09c777b9ac3b2e1fb633b2f1be62dc1cd5f816bf17423789e5a51e7cd1c6fbe8cc00b1b75135ad0714ee54214537";
      version = "1.2.0";

    };
    paths = [ src ];
  }

  rec {
    name = "crypto-random/crypto-random";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "crypto-random";
      groupId = "crypto-random";
      sha512 = "0551fba1c86871b1e70fe80679d78f11bec678ccad07f7b40cc7de1e97a3ec11f7df43dd86508869765cb1a6b2b5aa5cac6d10a5d319a26df8f513ec2bfa29e8";
      version = "1.2.0";

    };
    paths = [ src ];
  }

  rec {
    name = "ring-codec/ring";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "ring-codec";
      groupId = "ring";
      sha512 = "38b9775a794831b8afd8d66991a75aa5910cd50952c9035866bf9cc01353810aedafbc3f35d8f9e56981ebf9e5c37c00b968759ed087d2855348b3f46d8d0487";
      version = "1.1.3";

    };
    paths = [ src ];
  }

  rec {
    name = "crypto-equality/crypto-equality";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "crypto-equality";
      groupId = "crypto-equality";
      sha512 = "54cf3bd28f633665962bf6b41f5ccbf2634d0db210a739e10a7b12f635e13c7ef532efe1d5d8c0120bb46478bbd08000b179f4c2dd52123242dab79fea97d6a6";
      version = "1.0.0";

    };
    paths = [ src ];
  }

  rec {
    name = "cheshire/cheshire";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "cheshire";
      groupId = "cheshire";
      sha512 = "762d678617e6b83951a219421091cb6ca44f2d73d7d11a2b595d978f43dff82527c2881ff3adb3507540e06cf49f098d2359b6158176c3fdfb9fedcf7919db04";
      version = "5.9.0";

    };
    paths = [ src ];
  }

  rec {
    name = "tigris/tigris";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "tigris";
      groupId = "tigris";
      sha512 = "5393fe3f656521a6760d289d9549ffb9e9c1a8a72b69878205d53763802afa8778f1cb8bed6899e0b9721de231a79b8b1254cc601c84f5374467f1cc4780a987";
      version = "0.1.1";

    };
    paths = [ src ];
  }

  rec {
    name = "jetty-client/org.eclipse.jetty";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "jetty-client";
      groupId = "org.eclipse.jetty";
      sha512 = "f2d70900e345694638a1b969729e4b3c9a3a19a563a53b2c74f26d7f67225dd03d57746a184f4df01fedc86aa9e62b62ca8bbe30ac4492d256ade5753ea2e772";
      version = "9.4.44.v20210927";

    };
    paths = [ src ];
  }

  rec {
    name = "core.match/org.clojure";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "core.match";
      groupId = "org.clojure";
      sha512 = "d69ed23bad115ed665b402886e1946fcecacbbfd05150f3eb66dce9ffc0381d0e02ed6f41cb390a6dfb74f4f26e3b0f6793dec38f6a4622dc53c0739d79f5f5e";
      version = "0.3.0";

    };
    paths = [ src ];
  }

  rec {
    name = "core.incubator/org.clojure";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "core.incubator";
      groupId = "org.clojure";
      sha512 = "5224bb749b1786213ecc5680012d1e89b75d183dca99391c575408ee3c8a2c5174a8b372de0b0bc4f89ffe842c16e1160f61948d7c1d93eca5a8885833634a81";
      version = "0.1.4";

    };
    paths = [ src ];
  }

  rec {
    name = "jetty-io/org.eclipse.jetty";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "jetty-io";
      groupId = "org.eclipse.jetty";
      sha512 = "cc89006ce0fa66d91d6a07cbf48d9dd88c1d847be6e31dba9c5acf20368b80d4c094fc153f52538b8f24e9b4f5b0b762901a4f42f307dba6c9b328694e355be4";
      version = "9.4.44.v20210927";

    };
    paths = [ src ];
  }

  rec {
    name = "tools.reader/org.clojure";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "tools.reader";
      groupId = "org.clojure";
      sha512 = "290a2d98b2eec08a8affc2952006f43c0459c7e5467dc454f5fb5670ea7934fa974e6be19f7e7c91dadcfed62082d0fbcc7788455b7446a2c9c5af02f7fc52b6";
      version = "1.3.2";

    };
    paths = [ src ];
  }

  rec {
    name = "opentracing-util/io.opentracing";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "opentracing-util";
      groupId = "io.opentracing";
      sha512 = "fbba29ff3d6018561077e9539ad9b72876424600eca3addb6a26981a4a3e52cb3dfd30f27945aff2b6c222c42454ce3ba67597171fd809a74c65b920f3a47c7a";
      version = "0.33.0";

    };
    paths = [ src ];
  }

  rec {
    name = "jsoup/org.jsoup";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "jsoup";
      groupId = "org.jsoup";
      sha512 = "85208c3fbb443bcc6992255e25ddff5e2feb81b7c72eb023e8e63654b656238853c1492057f7138c1599a92fe7258f08a2b92b188ffa5087b5ed58e52298a2c2";
      version = "1.7.1";

    };
    paths = [ src ];
  }

  rec {
    name = "javax.servlet-api/javax.servlet";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "javax.servlet-api";
      groupId = "javax.servlet";
      sha512 = "32f7e3565c6cdf3d9a562f8fd597fe5059af0cf6b05b772a144a74bbc95927ac275eb38374538ec1c72adcce4c8e1e2c9f774a7b545db56b8085af0065e4a1e5";
      version = "3.1.0";

    };
    paths = [ src ];
  }

  rec {
    name = "slf4j-api/org.slf4j";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "slf4j-api";
      groupId = "org.slf4j";
      sha512 = "f9b033fc019a44f98b16048da7e2b59edd4a6a527ba60e358f65ab88e0afae03a9340f1b3e8a543d49fa542290f499c5594259affa1ff3e6e7bf3b428d4c610b";
      version = "1.7.36";

    };
    paths = [ src ];
  }

  rec {
    name = "websocket-server/org.eclipse.jetty.websocket";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "websocket-server";
      groupId = "org.eclipse.jetty.websocket";
      sha512 = "d2ad318d3ff2e04124788901208eb5c9c4fc18c4330425c3bbc035b5a111e82dd8f908993218c8b5f2dbb0d8d48146f02a226e278906c34c669bb12d76edfb61";
      version = "9.4.44.v20210927";

    };
    paths = [ src ];
  }

  rec {
    name = "metrics-jmx/io.dropwizard.metrics";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "metrics-jmx";
      groupId = "io.dropwizard.metrics";
      sha512 = "706f7428b967923d2792b0587684e972b1404d663a6ac3d661772a57edf096f0de0efac8bbfcead4576c008b096c33f77499e8f193ccbb8b072d7aa6e6d7a40d";
      version = "4.1.0";

    };
    paths = [ src ];
  }

  rec {
    name = "websocket-common/org.eclipse.jetty.websocket";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "websocket-common";
      groupId = "org.eclipse.jetty.websocket";
      sha512 = "7b4079393a5ee673a842436a9a4ed976ad6d5031a7a10ef061a4f4b4e139e704ee0bba8ce1c61523a58e4ba132cea79e4159a52f378c36a80d43f414f857e0da";
      version = "9.4.44.v20210927";

    };
    paths = [ src ];
  }

  rec {
    name = "pedestal.route/io.pedestal";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "pedestal.route";
      groupId = "io.pedestal";
      sha512 = "43ec8e185a6a88a735e444fe7ab9f246258394f660efbe069744f7e0c4e7cc77c60ab85fbb3d113bf7af3f2483d7da83eb48e7f85ed8381dbec1b723bbe16949";
      version = "0.5.10";

    };
    paths = [ src ];
  }

  rec {
    name = "core.memoize/org.clojure";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "core.memoize";
      groupId = "org.clojure";
      sha512 = "e1c5104ac20a22e670ccb80c085ce225c168802829668e91c316cbea4f8982431a9e2ac7bfa5e8477ef515088e9443763f44496633c8ee1e416f7eb8ddfefb88";
      version = "0.5.9";

    };
    paths = [ src ];
  }

  rec {
    name = "data.priority-map/org.clojure";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "data.priority-map";
      groupId = "org.clojure";
      sha512 = "450e18bddb3962aee3a110398dc3e9c25280202eb15df2f25de6c26e99982e8de5cf535fe609948d190e312a00fad3ffc0b3a78b514ef66369577a4185df0a77";
      version = "0.0.7";

    };
    paths = [ src ];
  }

  rec {
    name = "jetty-server/org.eclipse.jetty";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "jetty-server";
      groupId = "org.eclipse.jetty";
      sha512 = "fb3e66406dd7fbaaebeb323dc7cf120b81e359f2d37f8b54cf21a0542bfd34b8a8931adce12c5928efc3a2ba196c660c8c842c3eacfac0bc25b03a114a4f7f62";
      version = "9.4.44.v20210927";

    };
    paths = [ src ];
  }

  rec {
    name = "ring-core/ring";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "ring-core";
      groupId = "ring";
      sha512 = "38d7214a3fc1b80ab55999036638dd1971272e01bec4cb8e0ee0a4aa83f51b8c41ba8a5850b0660227f067d2f9c6d75c0c0737725ea02762bbf8d192dc72febe";
      version = "1.9.4";

    };
    paths = [ src ];
  }

  rec {
    name = "core.cache/org.clojure";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "core.cache";
      groupId = "org.clojure";
      sha512 = "464c8503229dfcb5aa3c09cd74fa273ae82aff7a8f8daadb5c59a4224c7d675da4552ee9cb28d44627d5413c6f580e64df4dbfdde20d237599a46bb8f9a4bf6e";
      version = "0.6.5";

    };
    paths = [ src ];
  }

  rec {
    name = "asm-all/org.ow2.asm";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "asm-all";
      groupId = "org.ow2.asm";
      sha512 = "462f31f8889c5ff07f1ce7bb1d5e9e73b7ec3c31741dc2b3da8d0b1a50df171e8e72289ff13d725e80ecbd9efa7e873b09870f5e8efb547f51f680d2339f290d";
      version = "4.2";

    };
    paths = [ src ];
  }

  rec {
    name = "core.async/org.clojure";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "core.async";
      groupId = "org.clojure";
      sha512 = "160a77da25382d7c257eee56cfe83538620576a331e025a2d672fc26d9f04e606666032395f3c2e26247c782544816a5862348f3a921b1ffffcd309c62ac64f5";
      version = "1.5.648";

    };
    paths = [ src ];
  }

  rec {
    name = "jackson-dataformat-smile/com.fasterxml.jackson.dataformat";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "jackson-dataformat-smile";
      groupId = "com.fasterxml.jackson.dataformat";
      sha512 = "409c39b581fa758a65e1e5344f08ac5607f5e3af41c5a307e61f2d0e54ba85487cfe6a6bbbeaa89a99c9fca0240c9dc5676cb50635378ae796570eb3bb427fe0";
      version = "2.9.9";

    };
    paths = [ src ];
  }

  rec {
    name = "jaxb-api/javax.xml.bind";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "jaxb-api";
      groupId = "javax.xml.bind";
      sha512 = "0c5bfc2c9f655bf5e6d596e0c196dcb9344d6dc78bf774207c8f8b6be59f69addf2b3121e81491983eff648dfbd55002b9878132de190825dad3ef3a1265b367";
      version = "2.3.0";

    };
    paths = [ src ];
  }

  rec {
    name = "jetty-alpn-server/org.eclipse.jetty";
    src = fetchMavenArtifact {
      inherit repos;
      artifactId = "jetty-alpn-server";
      groupId = "org.eclipse.jetty";
      sha512 = "53043e80612923fe519c99ddeee401368a4f96f6e14b9b9523e0f3dbdc4636ea6496ebce6c38d7f73946bd3072651ed9bdc55b52d13be123b9e1e31669237cce";
      version = "9.4.44.v20210927";

    };
    paths = [ src ];
  }

  ];
  }
  