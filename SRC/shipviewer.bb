; This one isnt used anymore...
; Its original purpose was to display meshes
; in the way they would be rendered ingame.

path$ = CommandLine()
If path$ <> "" Then 
	If Left(path$,1) = Chr(34) Then path$ = Mid(path$,2,Len(path$)-2)
EndIf
AppTitle "ShipViewer"

.restart

If vollbild = 0
	Graphics3D 640,480,32,2
Else
	Graphics3D 1024,768,32,1
EndIf

SetBuffer BackBuffer()

cpiv = CreatePivot()
cam=CreateCamera(cpiv)
AmbientLight 10,10,10

ChangeDir SystemProperty("APPDIR")

If FileType(path$) = 0 Then
	If FileType(path$+".b3d") <> 0 Then	
		path = path + ".b3d"
	EndIf
EndIf

	source$= "GFX/Environ/sky3.png"
	tex = LoadTexture( source$,128 )
	SetCubeMode tex,3
	cam_skysprite	= CreateSprite(cam)
	EntityTexture cam_skysprite, tex
	MoveEntity cam_skysprite,0,0,100
	EntityOrder cam_skysprite, 10
	EntityFX cam_skysprite,1+8
	ScaleSprite cam_skysprite, 100,100

Global shi_specular = LoadTexture("GFX/Environ/spec.png",128)
TextureBlend shi_specular,3
Global shi_specular2 = LoadTexture("GFX/Environ/spec2.png",128)
SetCubeMode shi_specular2,2
TextureBlend shi_specular2,5

TextureFilter "sphere",64
Mesh 		= LoadMesh(path,0)
ClearTextureFilters 
If mesh
	EntityFX mesh,1
	EntityTexture mesh,shi_specular2,0,2
	EntityTexture mesh,shi_specular,0,3
	PositionEntity cam,0,0,-MeshDepth(mesh)*1.7
EndIf

TurnEntity cpiv,90,0,180

RenderWorld
active = 0

While Not KeyHit(1)
	If KeyDown(28) And KeyDown(56) Then vollbild = 1 - vollbild : Goto restart
	If MouseHit(1) Then
		mxs = MouseXSpeed()
		mys = MouseYSpeed()
		active = 1
	EndIf
	mxs = MouseXSpeed()
	mys = MouseYSpeed()
	If MouseDown(1)
		TurnEntity cpiv,mys,-mxs,0
	Else
		active = 0
	EndIf
	
	If active = 0 And vollbild = 1
		TurnEntity cpiv,0,.2,0
		active = 1
	EndIf
	
	If active Then RenderWorld
	Flip
	If active = 0 Then Delay 100
Wend
End
;~IDEal Editor Parameters:
;~C#Blitz3D