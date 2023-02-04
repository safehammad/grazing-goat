# The Goat of Monte Carlo

This application solves the [Grazing Goat Problem](https://mathworld.wolfram.com/GoatProblem.html),
a recreational mathematical problem described as follows:

"Imagine a circular fence that encloses an area of grass. If you tie a goat to the inside of the fence, how
long a rope do you need to allow the animal access to exactly half that area?"

Despite this puzzle existing for over 250 years, a method to arrive at an exact solution was only
[found in 2020](https://www.quantamagazine.org/after-centuries-a-seemingly-simple-math-problem-gets-an-exact-solution-20201209/).

This application arrives at a very approximate solution using the [Monte Carlo Method](https://en.wikipedia.org/wiki/Monte_Carlo_method).
Random dots are fired in the coordinate space of the field for multiple trials of rope length. Each trial calculates the proportion of
the number of dots landing in the roped area compared to the number of dots landing in the full fenced area. When that proportion is
0.5, we have our solution! The resulting length of the rope is expressed as a multiple of the radius of the enclosed area.


![](https://raw.githubusercontent.com/safehammad/grazing-goat/main/animation.gif)

## Installation

Check out or download from https://github.com/safehammad/grazing-goat.

## Usage

To run:

    $ clojure -M -m safehammad.grazing-goat

## License

Copyright © 2020-2023 Safe Hammad

Distributed under the Eclipse Public License version 1.0.
