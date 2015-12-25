Const datad$="DATA\"
Include "util.bb"
Include "initgfx.bb"
;Graphics3D 1066,600,32,2
;SetBuffer BackBuffer()
Global menu_cam=CreateCamera()
Local Hintergrund=LoadSprite("gfx\gui\hintergrund.jpg",256)
PositionEntity hintergrund,0,0,4
ScaleSprite hintergrund,4,4
Global gfxd$="gfx\"

Include "menugui.bb"
Include "3dmouse.bb"
Include "aemodule/textlib.bb"

MGui_Init()

Local g.tgadget=MGui_CreateWindow.Tgadget(160,1,30,110, "", Null)
Local new_game.Tgadget=MGui_CreateButton.TGadget(1,1,28,8, "new Game", 100,100,155, g ,"")
Local Optionen.Tgadget=MGui_CreateButton.TGadget(1,11,28,8, "Options", 100,100,155, g ,"")
Local  Credits.Tgadget=MGui_CreateButton.TGadget(1,21,28,8, "Credits", 100,100,155, g ,"")
Local  Beenden.Tgadget=MGui_CreateButton.TGadget(1,31,28,8, "Close"  , 100,100,155, g ,"")
;EntityColor g\mesh,255,255,255
Mouse_create()

Repeat
	;MGUI_size(G,Rand(50,200),Rand(50,100))
	Mouse_update()
	MGui_Update()
	RenderWorld
	Flip 1
Until KeyHit(1)

End