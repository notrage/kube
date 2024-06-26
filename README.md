# Kube
This is a university project (in collaboration with [Thomas Civade](https://github.com/Luminosaa), [Côme Vincent](https://github.com/comejv), [Clément Corbalan](https://github.com/ClemCorb), [Seçkin Yağmur Ergin](https://github.com/xaelxx14) and [Axel Deleuze-Dordron](https://github.com/Stonksmen)) that aim to create a clone of the K3 classic game in Java.
## Installation
To run Kube on your local machine, make sure you have Java Development Kit (JDK) version 11 or later installed, along with Apache Maven.
1. Clone this repository to your local machine:

```bash
git clone git@github.com:notrage/kube.git
```
2. Navigate to the project directory:
```bash
cd kube
```
3. Build the project using Maven:
```bash
mvn clean package
```
4. Run the game:
```bash
java -jar target/kube-alpha.jar
```
Or double click on the jar file in the target directory.
## Features
- [x] Play using a graphical user interface.
- [x] Build a mountain in the first phase.
- [x] Place your pieces on the central mountain in the second phase.
- [x] Undo and redo moves.
- [x] Play against the computer.
- [x] Resize your window to fit your screen.
- [x] Save and load games.
- [x] Play against other players online.
## How to Play
The game is played in 2 phases.

The aim of the game is to prevent your opponent from making any more moves in the second phase. In the first phase, you must build a mountain from the pieces you are given. 
In the second phase, you place your pieces on the central mountain, taking turns with your opponent. The only condition is that at least one of the two bases must be the same color as your piece. When you play a piece on two pieces of the same color, you receive a penalty. In this case, your opponent must take one of your “accessible” pieces and add it to his or her reserve of “extra” pieces. Note that extra pieces can be played just like “accessible” pieces. There are two jokers, natural and white. White pieces allow you to pass your turn without playing on the central mountain. Natural pieces have no terrain constraints (they can, however, provoke a penalty). Any piece can be played over them.
## Acknowledgments
- Inspired by the classic K3 game.
- Built with Java and Maven.
## Credits
- [Volume icon by Styfico](https://thenounproject.com/icon/volume-6856436/)
- [Mute icon by Styfico](https://thenounproject.com/icon/volume-6856436/)
- [Gear icon by Styfico](https://thenounproject.com/icon/gear-6641296/)
- [Jomhuria font by KB Studio](https://fonts.google.com/specimen/Jomhuria)
- [HSL toolkit adapted from mjakson on github](https://gist.github.com/mjackson/5311256)
- [Auto build sound from Pixabay](https://pixabay.com/sound-effects/small-rock-break-194553/)
- [Remove sound from Pixabay](https://pixabay.com/sound-effects/swoop1-108087/)
- [Build and swap sounds from Sapsplat](https://www.zapsplat.com/sound-effect-category/rock/)