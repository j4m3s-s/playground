{
  description = "Playground flake";

  inputs = {
    flake-compat = {
      url = "github:edolstra/flake-compat";
      flake = false;
    };

    gitignore = {
      url = "github:hercules-ci/gitignore.nix";
      inputs.nixpkgs.follows = "nixpkgs";
    };

    nixpkgs.url = "github:NixOS/nixpkgs/nixos-22.11";

    tvlkit = {
      url = "github:tvlfyi/kit/canon";
      flake = false;
    };

    nix2container = {
      url = "github:nlewo/nix2container";
      inputs.nixpkgs.follows = "nixpkgs";
    };
  };

  outputs = {
    self
    , nixpkgs
    , tvlkit
    , flake-compat
    , gitignore
    , nix2container
  } @ inputs:
  let
    system = "x86_64-linux";
    pkgs = import nixpkgs { inherit system; };
    lib = pkgs.lib;

    tvllib = import tvlkit { inherit pkgs; };


    incompleteArgsNoRepo = inputs // {
      version = if builtins.hasAttr "shortRev" self then self.shortRev else "dirty";
      nix2container = nix2container.packages.${system}.nix2container;
      inherit
        pkgs
        lib
        system
        ;
      inherit (gitignore.lib) gitignoreSource;
      # For external repos packages that are not from nixpkgs
      third_party = {
        inherit (nix2container.packages.${system}) skopeo-nix2container;
      };
    };
    incompleteArgsNoLib = incompleteArgsNoRepo // { repo = incompleteArgsNoRepo; };
    args = incompleteArgsNoLib // { inherit myLib; };

    myLib = import ./lib incompleteArgsNoLib;

    # CI targets are currently opt-in since most of this repo doesn't build with
    # Nix currently.
    isCITarget = node: (node ? outPath) && (node.meta.ci.build or false);

    repo = self.my.${system};
  in rec {

    my.${system} = tvllib.readTree {
      path = ./.;
      inherit args;
    };
    ci.targets = repo.third_party.tvllib.mygather isCITarget my.${system};

    ci.gcroot = pkgs.writeText "repo-gcroot"
      (builtins.concatStringsSep "\n"
        (lib.flatten
          (map (p: map (o: p.${o}) p.outputs or [ ]) # list all outputs of each drv
            self.ci.targets)));

    devShell.${system} = pkgs.mkShell {
      # Django default environment
      DJANGO_ENV = "dev";
      # VueJS api endpoint for dev (which is Django usually)
      VUE_APP_API_ENDPOINT_URL = "http://localhost:8000";

      buildInputs = with pkgs; [
        gcc
        gnumake
        gtest

        # tmpl
        python3
        poetry
        yarn
        nodejs
      ];
    };
  };
}
