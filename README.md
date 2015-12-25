DGX BattleSpace 9142
==========

Benjamin "Mr.Keks" Bisping - <http://mrkeks.net> - <ben@mrkeks.net>
Steffen "CdV" Altmeier
DerHase - <http://www.budapestfastfood.com>
Sebastian Schell - <http://sebastian-schell.de>

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

Minimum...
| ---- | --------------------: |
| CPU  |  1 GHz*               |
| RAM  |  1 GB                 |
| Disk |  100 MB
| GPU  |  128 MB Grafikspeicher DirektX 7 Unterst�tzung
  (Achtung, seit Windows Vista ist es Gl�ckssache,
  ob DX7-Spiele wie DGX laufen :/;
  In Wine auf Ubuntu lief es ganz gut) |
\* Multicore hilft nicht, besser einen Kern mit hohem Takt! 2 Ghz
  machen das Spiel schon fl�ssiger, die ganzen Schusskollisionen
  sind seeehr hungrig.


## 2. MANUAL: HOW TO PLAY

Gespielt wird wie in Battlefield, bis der Gegner keine Tickets hat. Verliert
eine Fraktion ein Schiff, so verliert sie auch Punkte. Je nach Mission l�sst
die Einnahme bestimmter Stellungen oder das Ausf�hren gewisser Auftr�ge den
Gegner schneller Punkte verlieren.

Diese Stellungen, im Spiel Sprungtore oder JumpGates genannt, dienen zun�chst
als Einstiegspunkte f�r Spieler, sowie f�r nicht-Spieler-gesteuerte Kreuzer
und station�re Gesch�tze. Nicht alle Sprungtore lassen sich erobern. Nicht
eroberbare Sprungtore sind durch ein Verbots-Zeichen gekennzeichnet.
Sprungtore, die zus�tzliche Kreuzer bringen, erkennt man an gr�nen Pfeilen.

Bevor es in den Kampf geht, w�hlt man ebenfalls battlefieldtypisch eines der
Sprungtore als Einstiegspunkt und eine Schiffsklasse. Es gibt vier Klassen:
J�ger zum gew�hnlichen Kampf, Bomber zum Angriff auf Kreuzer und Stationen,
Aufkl�rer f�r schnelle Schl�ge an unerwarteten Positionen (Aufkl�rer k�nnen
eigene Schiffe f�r den Feindradar unsichtbar machen, bzw. unsichtbare
Feindschiffe aufsp�ren) und Unterst�tzungsschiffe zum Aufmunitionieren und
Regenerieren von Freundschiffen.

Gek�mpft wird so �hnlich wie in Freespace, Freelancer und anderen
Weltraumspielen. Mit `WASD` regelt man den Schub, `Q` und `E` rollen, Tab
aktiviert die Nachbrenner. Mit der mittleren Maustaste schaltet man die
gew�nschten Ziele durch, mit der linken und rechten feuert man auf das
anvisierte Schiff. Das gew�hlte Ziel wird durch ein Rechteck hervorgehoben.
Berfindet es sich au�erhalb des Bildschirms, zeigt ein Pfeil in seine
Richtung.

Solltest du im Gefecht die �bersicht verlieren, hilft meist ein kurzer
Blick auf die Radarkarte mittels `M`. Dein eigenes Schiff wird dort mit
einem leichten wei�en Schimmern hervorgehoben. Als einziger menschlicher
Spieler in einem Team darfst du den Bots Befehle erteilen. (Linke Maustaste
w�hlt aus, rechte schickt los, `X` nimmt Befehle zur�ck.) Au�erdem
kannst du mittels `,` die Ansicht umschalten, sodass die Orientierung
leichter f�llt.

Besonders f�r den Multiplayer ist interessant, dass sich mittels `Enter`
die Chat-Konsole �ffnen l�sst. Auch im Single-Player ist diese aber n�tzlich,
um die Botverteilung zwischen den Teams zu justieren und �hnliches. Eine
Liste der Befehle wird angezeigt, wenn du `\help` eingibst.
`F1` zeigt eine Liste der Spieler und ihrer Punkte an.

Viel Spa� beim Spielen!


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