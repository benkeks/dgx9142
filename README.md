DGX BattleSpace 9142
==========

* Benjamin "Mr.Keks" Bisping - <http://mrkeks.net> - <ben@mrkeks.net>
* Steffen "CdV" Altmeier
* DerHase - <http://www.budapestfastfood.com>
* Sebastian Schell - <http://sebastian-schell.de>

-------------------

## 0. PREFACE: TEN YEARS ####################

It's been more than ten years since I started working at DGX9142 on the
25 March 2004. Actually, I planned to make a little game over the
Easter break of 2004. I was 15 years old at that time.

Since then, lots of stuff has happened, but the completion of DGX9142 is
not among it. Battlefied 1942, which inspired this game, has become some
kind of a classic. DirectX 7, which is used for the graphics, has long
reached the end of its life cycle. I and all the other people who have
contributed to DGX9142 do quite different things by now. For my part,
I am more into theoretical computer science, print design and political
stuff today.

Over the last six years, DGX9142 has hardly been developed any further.
The last public alpha was released in March 2008. So I and Steff decided
to abandon the project. We don't want it to rot on our hard drives.
Thus we release it. This final release is ; in a way the most unfinished
version of DGX9142 that has ever been published: Some textures are
missing, the net code most likely won't work properly, there is no real
balancing etc. etc.

We hope you enjoy the game anyway. :)
   -- 25 December 2015, Benjamin Bisping (aka Mr.Keks)

## 1. SYSTEM REQUIREMENTS

Minimum | Needed
------- | --------------------
CPU     |  1 GHz
RAM     |  1 GB
Disk    |  100 MB
GPU     |  128 MB GPU Memory, DirektX 7 Support

On newer Windows versions there might be problems with DX7-games. I only tested the game with Wine on Ubuntu, where it works fine.

## 2. MANUAL: HOW TO PLAY

Gespielt wird wie in Battlefield, bis der Gegner keine Tickets hat. Verliert
eine Fraktion ein Schiff, so verliert sie auch Punkte. Je nach Mission lässt
die Einnahme bestimmter Stellungen oder das Ausführen gewisser Aufträge den
Gegner schneller Punkte verlieren.

Diese Stellungen, im Spiel Sprungtore oder JumpGates genannt, dienen zunächst
als Einstiegspunkte für Spieler, sowie für nicht-Spieler-gesteuerte Kreuzer
und stationäre Geschütze. Nicht alle Sprungtore lassen sich erobern. Nicht
eroberbare Sprungtore sind durch ein Verbots-Zeichen gekennzeichnet.
Sprungtore, die zusätzliche Kreuzer bringen, erkennt man an grünen Pfeilen.

Bevor es in den Kampf geht, wählt man ebenfalls battlefieldtypisch eines der
Sprungtore als Einstiegspunkt und eine Schiffsklasse. Es gibt vier Klassen:
Jäger zum gewöhnlichen Kampf, Bomber zum Angriff auf Kreuzer und Stationen,
Aufklärer für schnelle Schläge an unerwarteten Positionen (Aufklärer können
eigene Schiffe für den Feindradar unsichtbar machen, bzw. unsichtbare
Feindschiffe aufspüren) und Unterstützungsschiffe zum Aufmunitionieren und
Regenerieren von Freundschiffen.

Gekämpft wird so ähnlich wie in Freespace, Freelancer und anderen
Weltraumspielen. Mit `WASD` regelt man den Schub, `Q` und `E` rollen, Tab
aktiviert die Nachbrenner. Mit der mittleren Maustaste schaltet man die
gewünschten Ziele durch, mit der linken und rechten feuert man auf das
anvisierte Schiff. Das gewählte Ziel wird durch ein Rechteck hervorgehoben.
Berfindet es sich außerhalb des Bildschirms, zeigt ein Pfeil in seine
Richtung.

Solltest du im Gefecht die Übersicht verlieren, hilft meist ein kurzer
Blick auf die Radarkarte mittels `M`. Dein eigenes Schiff wird dort mit
einem leichten weißen Schimmern hervorgehoben. Als einziger menschlicher
Spieler in einem Team darfst du den Bots Befehle erteilen. (Linke Maustaste
wählt aus, rechte schickt los, `X` nimmt Befehle zurück.) Außerdem
kannst du mittels `,` die Ansicht umschalten, sodass die Orientierung
leichter fällt.

Besonders für den Multiplayer ist interessant, dass sich mittels `Enter`
die Chat-Konsole öffnen lässt. Auch im Single-Player ist diese aber nützlich,
um die Botverteilung zwischen den Teams zu justieren und ähnliches. Eine
Liste der Befehle wird angezeigt, wenn du `\help` eingibst.
`F1` zeigt eine Liste der Spieler und ihrer Punkte an.

Viel Spaß beim Spielen!


## REDISTRIBUTION?

The game is freeware. You may spread it as you whish.

We also include the source with the game. It's not really "open source"
as the source is not "open" in the sense of: Easy to maintain by others.
(No documentation, poor structure, some lazy hacks, redundancies, out-dated
programing language etc.) Still: Feel free to use the source in any way you
like, be it in other projects or in a more polished version of DGX.

To build the source, you will need Blitz3d. It can be obtained from
<http://blitzbasic.com> for free. Its source code can also be found on
[GitHub](https://github.com/blitz-research/blitz3d).

If you continue DGX and publish your work, please include the original credits
with it. The third party parts within DGX are marked as such by comments.
The artwork is by the credited people or from freeware archives. You may use
it in derived versions of DGX. If you want to use it in another context,
you may have to ask the very authors for permission.
