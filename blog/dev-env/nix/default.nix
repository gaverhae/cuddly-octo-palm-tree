{ system ? builtins.currentSystem }:

let
  pkgs = import ./nixpkgs.nix { inherit system; };

  # Selects "bin" output from multi-output derivations which are has it. For
  # other multi-output derivations, select only the first output. For
  # single-output generation, do nothing.
  #
  # This ensures that as few output as possible of the tools we use below are
  # realized by Nix.
  selectBin = pkg:
    if pkg == null then
      null
    else if builtins.hasAttr "bin" pkg then
      pkg.bin
    else if builtins.hasAttr "outputs" pkg then
      builtins.getAttr (builtins.elemAt pkg.outputs 0) pkg
    else
      pkg;

in rec {
  inherit pkgs;
  tools = pkgs.lib.mapAttrs (_: pkg: selectBin pkg) (rec {
    aws = pkgs.awscli;
    terraform = pkgs.terraform_0_14.withPlugins (p: with p; [
    ]);
    goaccess = pkgs.goaccess;
    lnav = pkgs.lnav;

    # Java
    #jdk    = pkgs.jdk8;
    #java   = jdk;
    #javac  = jdk;
    #jinfo  = jdk;
    #jmap   = jdk;
    #jstack = jdk;
    #jar    = jdk;

    #sass = pkgs.sass;

  });

}
