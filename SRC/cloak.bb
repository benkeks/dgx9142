
Type cloak
	Field s.ship
	Field mesh
	Field tex
	Field update
End Type

Global cloak_res=128
Global cloak_cam

Function Cloak_Init()
	cloak_cam = CreateCamera()
	HideEntity cloak_cam
End Function

Function Cloak_CreateCloak(s.ship)
	c.cloak = New cloak
	c\s		= s
	c\mesh	= CreateSphere(8,s\piv)
	c\tex	= CreateTexture(cloak_res,cloak_res,1+128+256)
	EntityTexture c\mesh,c\tex
	SetCubeMode c\tex,2
	EntityFX c\mesh,1
	ScaleEntity c\mesh,c\s\shc\cloak,c\s\shc\cloak,c\s\shc\cloak
End Function

Function Cloak_Remove(s.ship)
	For c.cloak = Each cloak
		If c\s = s
			FreeEntity c\mesh
			FreeTexture c\tex
			Delete c
		EndIf
	Next
End Function

Function Cloak_Update()
	For c.cloak = Each cloak
		c\update = c\update + main_mscleft
		If c\update > 80
			HideEntity cc_cam
			HideEntity c\s\piv
			Cloak_UpdateCubemap(c\tex,cloak_cam,c\mesh)
			;EntityTexture c\mesh,c\tex
			;SetCubeMode c\tex,1
			TurnEntity c\mesh,0,1,0
			ShowEntity c\s\piv
			ShowEntity cc_cam
		EndIf
	Next
End Function

Function Cloak_UpdateCubemap(tex,camera,entity) ; this function isnt by me. dont know who wrote it
   SetBuffer BackBuffer()				; nevertheless, thx to him for preventing me from havin' to write it on my own ^^
	tex_sz=TextureWidth(tex)
   ShowEntity camera
   
   HideEntity entity
   PositionEntity camera,EntityX#(entity,1),EntityY(entity,1),EntityZ(entity,1)
   PositionEntity env_sky,EntityX(camera,1),EntityY(camera,1),EntityZ(camera,1)
   CameraViewport camera,0,0,tex_sz,tex_sz

   SetCubeFace tex,0
   RotateEntity camera,0,90,0
   RenderWorld
   CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(tex)

   SetCubeFace tex,1
   RotateEntity camera,0,0,0
   RenderWorld
   CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(tex)

   SetCubeFace tex,2
   RotateEntity camera,0,-90,0
   RenderWorld
   CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(tex)

   SetCubeFace tex,3
   RotateEntity camera,0,180,0
   RenderWorld
   CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(tex)

   SetCubeFace tex,4
   RotateEntity camera,-90,0,0
   RenderWorld
   CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(tex)

   SetCubeFace tex,5
   RotateEntity camera,90,0,0
   RenderWorld
   CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(tex)

   ShowEntity entity
   HideEntity camera
   PositionEntity env_sky,EntityX(cc_cam,1),EntityY(cc_cam,1),EntityZ(cc_cam,1)
End Function