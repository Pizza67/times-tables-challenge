# Times Tables Race
A simple Android app for training in times tables.

<img src="https://github.com/Pizza67/times-tables-challenge/assets/13993235/9bc032e9-02d0-4ef8-8d82-7055c275cd27" width="200" ><img src="https://github.com/Pizza67/times-tables-challenge/assets/13993235/7843ed7e-e555-435a-8441-bacf67fabd1f" width="200"><img src="https://github.com/Pizza67/times-tables-challenge/assets/13993235/a0eae962-d7f9-47d2-b0d2-1252ccecb4d1" width="200"><img src="https://github.com/Pizza67/times-tables-challenge/assets/13993235/e4d6828d-5352-47fe-a3ca-22a7455de12a" width="200"><img src="https://github.com/Pizza67/times-tables-challenge/assets/13993235/838346b5-8f3f-4d42-97b4-a794afedac80" width="200">

## Rounds 
A player must try to answer up to 20 times tables questions in 30 seconds.

Based on the final score the player can reach one of the following levels:

* **Student**: Score is up to 6
* **Expert**: Score is between 7 and 12
* **Champion**: Score is between 13 and 17
* **Superhero**: Score is between 18 and 19
* **Cosmic Legend**: Score is 20

The resulting score of each round is used to track progress and measure the improvements.

## Achievements
In the journey the player may unlock the following achievements which are awarded based on a player's average score across all completed rounds.

* **Novice Learner**: Average score is below 8, or you have played fewer than 20 rounds
* **Rising Genius**: Average score is below 12 and you have played fewer than 40 rounds
* **Sparkling Champion**: Average score is below 15 and you have played fewer than 80 rounds
* **Supereme Hero**: Average score is below 18 and you have played fewer than 100 rounds
* **Galactic Master**: Average score of 18 or higher and you have played at least 100 rounds

_Pro Tip:_ To boost your average score, try replaying your lowest-scoring rounds to improve those results.

## Challenge Mode
Generate a unique round ID and share it with your opponents via QR code or a custom URL. 
Each player can start the round whenever they're ready, but everyone faces the same challenging questions.

_Remember, your performance in Challenge Mode rounds counts towards your overall average score!_

## Game variants
Adjust certain game settings without altering the core path to leveling up and unlocking achievements.

* **Go Beyond 9**: Extend the times tables up to 12 for an extra challenge. (Default: Times tables up to 9)
* **Self-Sabotage**: With this option enabled, your score for each round is permanently recorded, even if you replay it and get a lower score. (Default: Only your highest score for each round is saved)
* **Track time left**: In this mode, your best round score will factor in the time you have remaining after answering all questions. The faster you are, the higher your potential score! (Default: Time remaining does not affect the score)

## Motivations
I created this app while working through the [Android Basics With Compose](https://developer.android.com/courses/android-basics-compose/course) course. One exercise challenged me to build a simple app from scratch using the knowledge I had gained up to that point. At that stage, I hadn't yet reached the lesson on fetching data from the internet. This sparked my curiosity about creating a polished and useful app without requiring the internet permission (in fact, this app requests no permissions at all!).

It turned out that the initial version of the app, without levels or achievements and simply focused on playing rounds, was a big hit with my kids! They encouraged me to add more features and make it available to others – while keeping it privacy-safe, completely ads-free, and totally free of charge.

Guess this won't be the app that'll make our family rich, but no father could refuse such a request! 😊
