{ lib, pkgs, system, ... }:

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


  mkHostArgs = specialArgs: path: {
    inherit system;
    specialArgs = specialArgs;
    modules = [
      path
    ];
  };



  # imports a path and all its file.nix as { file = pkg } and folder f having
  # default.nix as { f = pkg }
  pkgsImport = path: let
    # { toto.nix = "regular", tata = "folder"}
    entries = builtins.readDir path;
    # [toto.nix tata]
    entriesNames = builtins.attrNames entries;
  in
    # { toto, tata}
    builtins.listToAttrs (map (v: let
      file = (path + ( "/" + v));
    in {
      name = (lib.strings.removeSuffix ".nix" v);
      value = pkgs.callPackage file { };
    }) entriesNames);
}
