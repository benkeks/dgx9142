Global Mouse_pointer%
Global Mouse_X#, Mouse_Y#
Global Mouse_gfx$= gfxd$+"menu\mouse.png"
Global Mouse_gh#
Const Mouse_gw#=100.0
Const mouse_size#=5.0



Function Mouse_Update()
	Mouse_x=(Float(MouseX())/main_hwidth-1.0)*Mouse_gw#
	Mouse_y=(-Float(MouseY())/main_hheight+1.0)*Mouse_gh#
	PositionEntity Mouse_pointer%,mouse_X,mouse_Y,100
	TurnEntity mouse_pointer,0,0,main_gspe
	HidePointer
End Function



Function Mouse_init(parent)
	Local tex		= LoadTexture(Mouse_gfx$,1+2)
	Mouse_pointer%		= CreateMesh(parent)
	Local sur		= CreateSurface(Mouse_pointer%)
	
	Local vert[3]
	vert[0]	= AddVertex(sur,	-1*mouse_size#,1*mouse_size#,0,		0,0)
	vert[1]	= AddVertex(sur,	1*mouse_size#,1*mouse_size#,0,		1,0)
	vert[2]	= AddVertex(sur,	-1*mouse_size#,-1*mouse_size#,0,	0,1)
	vert[3]	= AddVertex(sur,	1*mouse_size#,-1*mouse_size#,0,		1,1)
	
	AddTriangle sur, vert[0], vert[2], vert[1]
	AddTriangle sur, vert[1], vert[2], vert[3]
	
	EntityFX Mouse_pointer%,1+8+16
	EntityOrder Mouse_pointer%,-13
	EntityColor Mouse_pointer%,255,255,255
	ScaleMesh mouse_pointer,.5,.5,.5
	
	EntityTexture Mouse_pointer%,tex
	PositionEntity Mouse_pointer%,0,0,100
	Mouse_gh# = 100.0/Float(main_width)*Float(main_height)

End Function