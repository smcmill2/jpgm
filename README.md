[![Build Status](https://travis-ci.org/smcmill2/jpgm.svg?branch=master)](https://travis-ci.org/smcmill2/jpgm) [![Coverage Status](https://coveralls.io/repos/github/smcmill2/jpgm/badge.svg?branch=master)](https://coveralls.io/github/smcmill2/jpgm?branch=master) [![Join the chat at https://gitter.im/java-pgm/Lobby](https://badges.gitter.im/java-pgm/Lobby.svg)](https://gitter.im/java-pgm/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# jpgm

A Java library for Probabilistic Graphical Models

## Roadmap
### 1.0.0
- Implement Discrete Factor Representation
    - Base Class
        - [x] Factor Product
    - Joint Probability Distribution
        - [ ] Independencies
- Implement Continuous Factor Representation
    - [ ] Linear Gaussian
- Bayesian Network Representation
    - [x] Basic structure and construction
    - [x] d-sep/independencies
- Markov Network
    - [ ] Basic structure and construction
    - [ ] d-sep
    - [ ] Partitioning/Normalization
- Inference
    - [ ] Variable Elimination
        - [ ] Sum-Product
        - [ ] Max-Product
- Toy Examples
    - Student example
        - [ ] Showcase current features for 1.0.0
- Documentation
    - [ ] Fully document all classes
- Testing
    - [ ] More robust testing of edge cases
        - [ ] Variables of the same name
        - [ ] Catching/Throwing errors
        
### 1.1.0
- Implement Discrete Factor Representation
    - Base Class
        - [ ] String Representations
        - [ ] inplace operations
    - Joint Probability Distribution
        - [ ] i-map of a network
        - [ ] p-map of a network
        - [ ] minimal i-map
- Implement Continuous Factor Representation
    - [ ] User defined distribution (dirichlet, etc.)
- Models
    - Factor Graph
        - [ ] Creation as a base class?
        - [ ] Conversions from other models
    - Bayesian Network Representation
        - [ ] Caching of graph queries (d-sep, trails)
        - [ ] i-equivalence
    - Markov Network
- Inference
    - Inference over generalized factor graph
- Toy Examples
    - Student example
        - [ ] Showcase current features for 1.1.0
- Documentation
    - [ ] Fully document all classes
    - [ ] Add documentation coverage
- Testing
    - [ ] More robust testing of edge cases
        - [ ] Variables of the same name
        - [ ] Catching/Throwing errors
- Logging
    - [ ] Decide upon and implement a logging structure
        - log4j/slf4j
