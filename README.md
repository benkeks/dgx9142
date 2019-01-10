DGX BattleSpace 9142
==========

* Benjamin "Mr.Keks" Bisping - <https://mrkeks.net> - <ben@mrkeks.net>
* Steffen "CdV" Altmeier
* DerHase - <http://www.budapestfastfood.com>
* Sebastian Schell - <http://sebastian-schell.de>

![Ingame Screenshot](https://mrkeks.net/pics/dgx2/Screen31.jpg)

## 1. INSTALLATION & SYSTEM REQUIREMENTS

Just unzip the game archive and run `DGX9142.exe`!

Minimum | Needed
------- | --------------------
CPU     |  1 GHz
RAM     |  1 GB
Disk    |  100 MB
GPU     |  128 MB GPU Memory, DirektX 7 Support

On newer Windows versions, there might be problems with DX7-games.

I mostly tested the game with Wine (32 bit edition) on Ubuntu and
Debian, where it works fine.

## 2. MANUAL: HOW TO PLAY

The basic gameplay follows the Battlefield principle.

Every team has a certain number of *tickets*. Killing enemy ships or holding special positions decreases
the enemy team's ticket counter. A team wins if the other team's counter hits zero.

Your ships spawn at *jump gates* held by your team.
You can conquer further jump gates by flying close to them while there is no enemy ship around. Some gates cannot be conquered -- they are marked with a restriction symbol.
Jump gates with a green arrow next to their name spawn NPC cruisers.

Before joining the battle at a jump gate, you chose out of four *ship classes*.

* *Fighter* for ordinary combat.
* *Bomber* to tackle big cruisers and stations.
* *Scout* for quick strikes at unexpected positions. (Scouts jam the enemy radar to a certain degree, but also counter enemy jamming.)
* *Support* which regenerate friendly ships or supply them with new ammunition.

The *fighting* is quite similar to FreeSpace, Freelancer and other space games.

* `WASD`: regulate the forward/backward/sideward thrust.
* `Q` and `E`: roll.
* `Tab`: enable the after burner
* Middle mouse button: lock targets (highlighted by a rectangle)
* Left/right mouse button: fire at the targeted ship
* `Ctrl` trigger special ability. (E.g. anti-missile EMP, mines, sentry gun placement – not every ship class has one!)

For *overview purposes* there are additional controls:

* `M`: show overview map (your ship is highlighted with white)
* `,`: toggle grid and hud info
* `C`: pan view without rotating the ship
* `Space`: zoom in

If you are your teams *commander* (as the only human player in a team you will be), you can use the overview map to give commands to your team:

* `Left mouse button`: select ships
* `Right mouse button`: give command (move to or attack)
* `X`: Cancel command

`Enter` activates the chat. You can also control parts of the game from there, the commands are explained if you enter `/help`.

`F1` shows a list of the active players and their score.

Have fun playing!

----------------------------------------------------------------

## 3. PROGRAMMER'S NOTE: TEN YEARS

It's been more than ten years since I started working on DGX9142 on
25 March 2004. Actually, I planned to make a little game over the
Easter break of 2004. I was 15 years old at that time.

Since then, lots of stuff has happened, but the completion of DGX9142 is
not among it. Battlefied 1942, which inspired this game, has become some
kind of a classic. DirectX 7, which is used for the graphics, has long
reached the end of its life cycle. I and all the other people who have
contributed to DGX9142 do quite different things by now. For my part,
I am more into theoretical computer science, print design and politics
today.

Over the last six years, DGX9142 has hardly been developed any further.
The last public alpha was released in March 2008. So I and Steff decided
to abandon the project. We don't want it to rot on our hard drives.
Thus we release it. This final release is in a way the most unfinished
version of DGX9142 that has ever been published: Some textures are
missing, the net code most likely won't work properly, and there is no
tweaked balancing.

We hope you enjoy the game anyway. :)
   -- 25 December 2015, Benjamin Bisping (aka Mr.Keks)

## 4. REDISTRIBUTION?

The game is freeware. You may spread it as you wish.

The source code can be found on <https://github.com/benkeks/dgx9142>, even
though it may not be easy to maintain by others. (No documentation, poor
structure, some lazy hacks, redundancies, out-dated programing language etc.)
Still: Feel free to use the source in any way you like, be it in other
projects or in a more polished version of DGX.

To build the source, you will need Blitz3d. It can be obtained from
<https://nitrologic.itch.io/blitz3d> for free. Its source code can also be found
on [GitHub](https://github.com/blitz-research/blitz3d).

If you're on Linux, you can use the Makefile. Just chage `BCC` in `Makefile` to
point to your `blitzcc.exe` and run `make run` to build and start the game.
For `make release`, you need `rsync`.

If you continue DGX and publish your work, please include the original credits
with it. The third party parts within DGX are marked as such by comments.
The artwork is by the credited people or from freeware archives. You may use
it in derived versions of DGX. If you want to use it in another context,
you may have to ask the very authors for permission.
