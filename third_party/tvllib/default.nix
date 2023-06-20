{ ... }:

rec {
  # Create a label from a target's tree location.
  mkLabel = target:
    let label = builtins.concatStringsSep "/" target.__readTree;
    in if target ? __subtarget
       then "${label}:${target.__subtarget}"
       else label;

  # Import from TVL
  # Helper function to fetch subtargets from a target. This is a
  # temporary helper to warn on the use of the `meta.targets`
  # attribute, which is deprecated in favour of `meta.ci.targets`.
  subtargets = node:
    let targets = (node.meta.targets or [ ]) ++ (node.meta.ci.targets or [ ]);
    in if node ? meta.targets then
      builtins.trace ''
        [1;31mWarning: The meta.targets attribute is deprecated.

        Please move the subtargets of //${mkLabel node} to the
        meta.ci.targets attribute.
        [0m
      ''
        targets else targets;

  # Adapted to also check subtargets of current attrset
  # from TVL's readTree
  mygather = eligible: node:
    if node ? __readTree then
      # Include the node itself if it is eligible.
      (if eligible node then [ node ] else [ ])
      # Include subtargets of the current node if they are eligible
      ++ (builtins.filter (val: eligible val) (builtins.attrValues node))
      # Include eligible children of the node
      ++ builtins.concatMap (mygather eligible) (map (attr: node."${attr}") node.__readTreeChildren)
      # Include specified sub-targets of the node
      ++ builtins.filter eligible (map
        (k: (node."${k}" or { }) // {
          # Keep the same tree location, but explicitly mark this
          # node as a subtarget.
          __readTree = node.__readTree;
          __readTreeChildren = [ ];
          __subtarget = k;
        })
        (subtargets node))
    else [ ];
}
