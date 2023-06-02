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
  };

  outputs = {
    self
    , nixpkgs
    , tvlkit
    , flake-compat
    , gitignore
  } @ inputs:
  let
    system = "x86_64-linux";
    pkgs = import nixpkgs { inherit system; };

    tvllib = import tvlkit { inherit pkgs; };

    incompleteArgs = inputs // {
      inherit
        pkgs
        ;
      inherit (gitignore.lib) gitignoreSource;
    };
    args = incompleteArgs // { repo = incompleteArgs; };
  in
  {

    my.${system} = tvllib.readTree {
      path = ./.;
      inherit args;
    };

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
