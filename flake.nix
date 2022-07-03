{
  description = "Playground flake";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-22.05";
  };

  outputs = { self, nixpkgs }:
  let
    system = "x86_64-linux";
    pkgs = import nixpkgs { inherit system; };
  in
  {

    devShell.${system} = pkgs.mkShell {
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
