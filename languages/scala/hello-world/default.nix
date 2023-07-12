{ lib, pkgs, myLib, ... }:

{
  shell = myLib.mkShell {
    packages = with pkgs; [ sbt ];
    shellHook = ''
    '';
  };
}
