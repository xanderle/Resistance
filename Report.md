# Project report (2000-3000 words): 50%**

- **Literature review of suitable techniques: 20%**

- **Rationale of selected technique: 10%**

- **Implementation description: 10%**

- **Validation: 10%**

## Suitable Techniques

### Monte-Carlo Methods

Monte-Carlo :
- Used to obtain estimations and predictions for scientific and mathematical problems that would be extremely difficult otherwise solve (analytically or exhaustively)
- Methods defining feature is use of repeated random sampling of a distribution.
- Given method to attain a value from an unknown probability distribution d, it is possible to estimate the expected value of the distribution itself through Monte-Carlo methodology.

### Tree-Search

- Game trees are a way of representing games decision processes as directed graphs. A complete game tree will comprise of every valid state reachable from the initial state, and all actions available at each of these states. States are represented by nodes and actions are represented by edges. Any complete game can be represented by a decision tree, which is a subtree of the complete game tree.

A complete tree can be extremely difficult to calculate in some cases, as every single possible action must be accounted for. Whilst initial set up and rules restrict the states in the game tree to a subset of those in the full state space, equivalent states accessible through various routes will be duplicated, so the game tree can be larger than the state space. Another factor to consider is branching factor of the game tree. The branching factor is the average number of children branching from each node.

This is useful when identifying the limitations of an AI using a tree-search to select the best action at each state. In complicated games like The Resistance, the game tree will be large and therefore hard to search through for appropriate actions.
### Monte-Carlo Tree-Search

Combines the repeated random sampling idea from Monte-Carlo methods, with a tree-search that does not require a complete game tree in order to select the best action. The game tree is constructed during the game from the results of numerous play outs where actions at decision nodes for controlled player are selected at random

Essentially there are four steps to a MCTS
1. Selecting a non-terminal root node from the root.
2. Creating one or more children at that node representing states reachable from the selected node.
A typical MCTS is essentially four steps
3. Conduct a random play out from one or more of the created nodes
4. Update the expected value of all nodes above the selected node

  Play outs are games played in full from a given node to a leaf node which represents a terminal state of the game. Terminal nodes are allocated some value, this value is back propagated to all nodes on the path from the terminal node tot he root node.

  Each nodes maintains a sum and a count of all values that reach it via this back propagation. It is then possible to calculate a value which approaches the expected value of the state it represented, by dividing the sum over the count, as count tends to infinity. These values can be used to decide which actions are most likely to be beneficial without search the full tree, and without using any defined evaluation function. As a result for larger games it is far more useful to use a MCTS then a Tree search, and MTCTS can be applied to games without having a developed theory. at page 17
### Opponent Modeling

- Refers to the application of statistical and probabilistic models to, adversarial, multi-agent environments such as board games and card games in order to identify opponent strategies, preferences, biases, and goals. Usually the goal of this modeling is to enable another agent to select strategies which counter his opponents, or to otherwise exploit their weaknesses
- Isn't suitable for Resistance because

  - Player's actions aren't always fully observable, cant record players actions with certainty. Makes it hard to compare their behavior to an opponent model

- Players may have knowledge of others states which we do not, modeling such intricacies should be possible, but it quite complicated

## Rationale of Selected Techniques

Reason for using bounder Opponent modeling is limited as data can't be saved over multiple games, hard to fully model opponents when some actions can't be read with full certainty

Methods involving game trees would take a lot of time.

As Resistance is a logic based game, with only a small amount of uncertainty in interactions with players of uncertain objectives, it is also a deterministic game. There are no random elements to game play. Thus it is possible to increase ones odds of victory by identifying enemies.

## Implementation Description

The Jigglypuff agent keeps track of all possible combinations of Resistance members and Government members in two collections. One collection's removals are based on the idea that spies will always sabotage, allowing for quick but inaccurate removals of combinations. The other collection's removals are based on the idea that spies don't always sabotage, and thus removes configurations at a slower but more accurate rate. At minimum this configuration will be reduced to a configuration that matches reality. The first collection provides a safe choice initially until it runs out, where the second collection is then used.

The two collections are called Optimistic and Pessimistic and are array lists of boolean arrays. Populated with every possible team combination of spies and resistance members where true indicates a spy and false represents a resistance member. Each index in the array corresponds to the other players as Jigglypuff assumes itself to be a Resistance member.

Whenever Jigglypuff is called to do_Vote() or do_Nominate() it removes configurations from optimistic and pessimistic based on the previous missions members and number of traitors. Configurations from Optimistic are removed when the number of spies doesn't equal the number of traitors and configurations from Pessimistic are removed when the number of spies is less than the number of sabotages.

For do_Vote, upon completing the removals, Jigglypuff checks whether the proposed team is in optimistic, fall back to pessimistic in the event that optimistic is empty. If the team exists, it votes to accept the mission, otherwise it votes to reject it.

For do_Nominate(), upon completing the removals, Jigglypuff random selects a configuration inside optimistic, or pessimistic if optimistic is empty. As all configurations are equally weighted inside the collections, it doesn't matter which one is chosen. Once the configuration is chosen Jigglypuff adds itself to the proposed team and members that are represented as being Resistance members are randomly chosen till the appropriate team size has been made. Members are randomly chosen from configurations to avoid favoring other players that are listed earlier.

As a spy member Jigglypuff will always chose to sabotage, and nominates and votes as if it was a Resistance member chosing teams from optimistic and pessimistic. However as it always adds itself to the nominated mission, it guarantees at least 1 spy is on the team. However as Jigglypuff is a spy, it is possible for pessimistic to be reduced to being completely empty. As an emergency case, when the pessimistic collection is completely emptied, Jigglypuff simply picks a random team including itself and proposes that.

## Validation


Initially Jigglypuff was implemented using only the two collections Optimistic and Pessimistic and run through simple test cases to verify that the agent was correctly removing configurations and acting upon these configurations.

A hypothetical situation was set up where the agent was player E, and the other members were ABCD. BC were spies. Spies always betray. Teams and failures were manually entered into the agent

A proposes a mission with B and C,
E votes to approve this mission,
Mission failed with 2 sabotages
The same team is proposed again
This time E rejects the team, indicating it learnt
Sending AB on a mission, 1 sabotage
Optimistic and Pessimistic now contain

[false,true,true,false] indicating that B and C are spies with 100% certainty

Another hypothetical situation was set up where the agent was player E, and the other members were ABCD. BC were spies. Spies don't always betray

A proposes a mission with B and C,
E votes to approve this mission,
Mission failed with 1 sabotage

Optimistic removes
[true,false,true,false]
[false,true,false,true]

These are the cases that indicate that not all spies tried to sabotage
Pessimistic removes
[true,false,false,true]

This is removed because at least B or C are spies

The same team is proposed again
This time E rejects the mission, indicating it learn

Sending AE on a mission, 0 sabotage

Optimistic Removes
[true,true,false,false]
[true,false,true,false]

Removes configurations where A is a spy

Pessimistic Removes nothing as A may be a spy and simply chose to not betray.

Sending AB on a mission, 1 sabotage
Optimistic
[false,false,true,true]

Removes because configuration says A and B aren't spies when 1 person sabotaged

Pessimistic
removes
[false,false,true,true]

Removes because configuration says A and B aren't spies when 1 person sabotaged.

Leaving in Optimistic
[false, true, false, true]

As C didnt betray when sent with BC it avoided detection, causing optimistic to think B and D are spies, properly identifying B but not D.

However pessimistic still contains
[true,true,false,false]
[true,false,true,false]
[false,true,true,false]
[false,true,false,true]

Which contains the valid solution of B and C as spies, however the next failure would result in Resistance winning the game.

First Check: Performance against Random

Wins:

Random: 0.40745

Jigglypuff: 0.59255

However any attempt at implementing expert rules for spy play on top of Jigglypuff resulted in lower win rates. Simple rules such as approving missions with at least one spy if spies only need one mission to win, rejecting missions when the entire team is spies and not sabotaging if every player on the mission is a spy dropped the bots overall win rate.

Attempts to Implement Expert Rules

ExpertJigglypuff: 0.4535

Jigglypuff : 0.5465

As a result Jigglypuff doesn't attempt to include any expert rules and is purely based on logic.
