[![Build Status](https://travis-ci.org/smcmill2/jpgm.svg?branch=master)](https://travis-ci.org/smcmill2/jpgm) [![Coverage Status](https://coveralls.io/repos/github/smcmill2/jpgm/badge.svg?branch=master)](https://coveralls.io/github/smcmill2/jpgm?branch=master) [![Join the chat at https://gitter.im/java-pgm/Lobby](https://badges.gitter.im/java-pgm/Lobby.svg)](https://gitter.im/java-pgm/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# jpgm

A Java library for Probabilistic Graphical Models

## Roadmap
### 1.0.0
- Implement Discrete Factor Representation
    - Base Class
        - [ ] Factor Product
        - [ ] String Representations
    - Joint Probability Distribution
        - [ ] Independencies
        - [ ] i-map of a network
        - [ ] p-map of a network
        - [ ] minimal i-map
- Implement Continuous Factor Representation
    - [ ] User defined distribution (dirichlet, etc.)
    - [ ] Linear Gaussian
- Bayesian Network Representation
    - [ ] Basic structure and construction
    - [ ] d-sep/independencies
    - [ ] Probability of events given evidence
- Markov Network
    - [ ] Basic structure and construction
    - [ ] d-sep
    - [ ] Partitioning/Normalization
    - [ ] Probability of events given evidence
- Inference
    - [ ] Variable Elimination
- Toy Examples
    - Student example
        - [ ] Showcase current features for 1.0.0
- Documentation
    - [ ] Fully document all classes
- Testing
    - [ ] More robust testing of edge cases
        - [ ] Variables of the same name
        - [ ] Catching/Throwing errors
