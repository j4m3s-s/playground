{ pkgs, myLib, ... }:

{
  shell = myLib.mkShell {
    # Django default environment
    DJANGO_ENV = "dev";

    buildInputs = with pkgs; [
      gcc
      gnumake
      gtest

      python3
      poetry
    ];
  };
}
