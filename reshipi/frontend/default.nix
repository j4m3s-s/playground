{ pkgs, myLib, ... }:

{
  shell = myLib.mkShell {
    # VueJS api endpoint for dev (which is Django usually)
    VUE_APP_API_ENDPOINT_URL = "http://localhost:8000";

    buildInputs = with pkgs; [
      gcc
      gnumake
      gtest

      yarn
      nodejs
    ];
  };
}
