{
  description = "Playground flake";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-22.05";

    tvlkit = {
      url = "github:tvlfyi/kit/canon";
      flake = false;
    };
  };

  outputs = {
    self
    , nixpkgs
    , tvlkit
  } @ inputs:
  let
    system = "x86_64-linux";
    pkgs = import nixpkgs { inherit system; };

    tvllib = import tvlkit { inherit pkgs; };
  in
  {

    my.${system} = tvllib.readTree {
      path = ./.;
      args = inputs;
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
