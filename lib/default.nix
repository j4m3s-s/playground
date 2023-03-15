{ lib, pkgs, ... }:

rec {
  ## Users
  # Function to create non mutable user with initial password
  mkUser = { keys ? [], initialHashedPassword, username }: {
    ${username} = {
      openssh.authorizedKeys.keys = keys;
      inherit initialHashedPassword;
    };
  };
  mkDefaultUsers = { initialHashedPassword, keys }: mkUser {
    username = "j4m3s";
    inherit
      initialHashedPassword
      keys
      ;
  } // mkUser {
    username = "root";
    inherit
      initialHashedPassword
      ;
  };

  ## Machine / Virtual Machine
  # This is an example of exposing a host derivation as a package.
  # Since technically everything is a derivation.
  mkHost = args: (import (pkgs.path + "/nixos/lib/eval-config.nix") args).config.system.build;
  mkHostVM = args: (mkHost args).vm;
  mkHostDerivation = args: (mkHost args).toplevel;
}
