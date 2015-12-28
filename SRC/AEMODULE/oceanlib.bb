; waterlib, Inarie aka Mr.Keks, 2005
; mail@inarie.de
; www.inarie.de
; www.projectblitz.de


Type wat ; water tile / water class
	Field bank ; bank that contains the water wave time
	Field width,depth ; width and depth of the mesh in quads
	Field mesh,sur,sur2
	Field brush,brush2
	Field cam,tex ; cubemappin cam and tex
	Field tex2, anim#
End Type

Type wat_surf ; a water circle ^^
	Field w.wat ; tile class
	Field size
	Field piv
End Type

Type wat_meshc ; a single mesh instance of the wat tile
	Field s.wat_surf
	Field mesh
End Type


Global wat_gamecam ; handle of the main cam
Global wat_whitecaps = 1 ; disable or enable whitecaps on high waves

Global wat_timer

; save all sin()-values in order to save some render time
Dim sin2#(255)
For i = 0 To 255
	Sin2(i) = Sin(i*360/255)
Next



; function to create a water class
Function Wat_CreateWater.wat(width, depth) 
	w.wat		= New wat
	
	w\width		= width
	w\depth		= depth
	
	w\mesh		= CreateMesh() 
	w\brush		= CreateBrush()
	w\brush2		= LoadBrush(gfxd$+"white.png",2,.5,.5)
	w\sur			= CreateSurface(w\mesh,w\brush)
	w\sur2		= CreateSurface(w\mesh,w\brush2)
	w\bank		= CreateBank(width * depth + 1)
	
	BrushFX w\brush,16
	BrushColor w\brush,70,90,110
	;BrushShininess w\brush,.3
	
	BrushFX w\brush2,1+2+16+32
	PaintSurface w\sur2,w\brush2
	
	HideEntity w\mesh
	
	ResizeBank w\bank,width*depth+1
	
	w\tex = CreateTexture(256,256,1+128+256)
	
	w\tex2 = LoadAnimTexture(gfxd$+"wateranim.bmp",256,124,124,0,25)
	ScaleTexture w\tex2,.1,.1
	TextureBlend w\tex2,3
	
	For i = 0 To width*depth
		PokeByte w\bank,i,Rand(128)+Sin(i*360/width)*128
	Next
	
	ScaleEntity w\mesh,2,1.001/256.001,2
	
	w\cam = CreateCamera()
	CameraFogMode w\cam,1
	CameraRange w\cam,1,1700
	CameraFogRange w\cam,1000,1700
	CameraFogColor w\cam,200,200,255
	HideEntity w\cam
	
	Return w.wat 
End Function 

; function to create a water circle
Function Wat_CreateSurface(w.wat,fieldsize)
	s.wat_surf	= New wat_surf
	s\w			= w
	s\size		= fieldsize
	s\piv		= CreatePivot()
	For x = -fieldsize To fieldsize
		For y = -fieldsize To fieldsize
			If y*y < fieldsize*fieldsize-x*x
				If LinePick((x-.5)*w\width,200,(y-.5)*w\depth,0,-180,0,w\depth/2) = 0
					m.wat_meshc = New wat_meshc
					m\s			= s
					m\mesh		= CopyEntity(w\mesh,s\piv)
					PositionEntity m\mesh,(x-(x/2*2<>x)*1)*(w\width)-2*x,0,(y-(y/2*2<>y)*1)*(w\depth)-2*y
					ScaleEntity m\mesh,2*(x/2*2<>x)-1,1.001/256.001,2*(y/2*2<>y)-1
					EntityPickMode m\mesh,2,1
				EndIf
			EndIf
		Next
	Next
	Return s\piv
End Function

Function Wat_UpdateAllWater()
	If wat_timer >= 200
		For w.wat = Each wat
			Wat_UpdateWater(w.wat)
		Next
	Else
		wat_timer = wat_timer + main_mscleft
	EndIf
End Function

; function to update all water tiles of a certain water class
Function Wat_UpdateWater(w.wat)
	Local vertex[3]
	Local vertex2[3]
	Local vertstobechanged[124*2]
	ClearSurface w\sur
	ClearSurface w\sur2
	
	For x = 0 To w\width-1
		For z = 0 To w\depth-1
			flukt = PeekByte(w\bank,1*(x*w\depth+z))
			PokeByte(w\bank,1*(x*w\depth+z),flukt+2)
		Next 
	Next 
	
	sw# = 64 ; wave height
	
	For x = 1 To w\width-2
		For z = 1 To w\depth-2
			vertex[0] = AddVertex(w\sur,x,sw*Sin2(PeekByte(w\bank,x*w\depth+z)),z,Float(x)/w\width,Float(z)/w\depth,1)
			vertex[1] = AddVertex(w\sur,x,sw*Sin2(PeekByte(w\bank,x*w\depth+z+1)),z+1,Float(x)/w\width,Float(z+1)/w\depth,1)
			vertex[2] = AddVertex(w\sur,x+1,sw*Sin2(PeekByte(w\bank,x*w\depth+w\depth+z+1)),z+1,Float(x+1)/w\width,Float(z+1)/w\depth,1)
			vertex[3] = AddVertex(w\sur,x+1,sw*Sin2(PeekByte(w\bank,x*w\depth+w\depth+z)),z,Float(x+1)/w\width,Float(z)/w\depth,1) 
			
			AddTriangle(w\sur, vertex[0], vertex[1], vertex[2]) 
		  	AddTriangle(w\sur, vertex[0], vertex[2], vertex[3])  
		
			If wat_whitecaps ; draw those cool whitecaps onto high waves
				temp = 0
				For i2 = 0 To 3
					If VertexY(w\sur,vertex[i2])>sw/1.5
						temp = temp + 1 
					EndIf 
				Next
				
				If temp > 0
					i3 = 0
					vertex2[0] = AddVertex(w\sur2,VertexX(w\sur,vertex[i3]),VertexY(w\sur,vertex[i3])+10,VertexZ(w\sur,vertex[i3]),VertexU(w\sur,vertex[i3]),VertexV(w\sur,vertex[i3]))
					VertexColor w\sur2,vertex2[0],255,255,255,(VertexY(w\sur,Vertex[i3])-sw/1.5)/sw*1.5
					i3 = 1
					vertex2[1] = AddVertex(w\sur2,VertexX(w\sur,vertex[i3]),VertexY(w\sur,vertex[i3])+10,VertexZ(w\sur,vertex[i3]),VertexU(w\sur,vertex[i3]),VertexV(w\sur,vertex[i3]))
					VertexColor w\sur2,vertex2[1],255,255,255,(VertexY(w\sur,Vertex[i3])-sw/1.5)/sw*1.5
					i3 = 2
					vertex2[2] = AddVertex(w\sur2,VertexX(w\sur,vertex[i3]),VertexY(w\sur,vertex[i3])+10,VertexZ(w\sur,vertex[i3]),VertexU(w\sur,vertex[i3]),VertexV(w\sur,vertex[i3]))
					VertexColor w\sur2,vertex2[2],255,255,255,(VertexY(w\sur,Vertex[i3])-sw/1.5)/sw*1.5
					i3 = 3
					vertex2[3] = AddVertex(w\sur2,VertexX(w\sur,vertex[i3]),VertexY(w\sur,vertex[i3])+10,VertexZ(w\sur,vertex[i3]),VertexU(w\sur,vertex[i3]),VertexV(w\sur,vertex[i3]))
					VertexColor w\sur2,vertex2[3],255,255,255,(VertexY(w\sur,Vertex[i3])-sw/1.5)/sw*1.5
					
					AddTriangle(w\sur2,vertex2[0],vertex2[1],vertex2[2])
					AddTriangle(w\sur2,vertex2[0],vertex2[2],vertex2[3])
				EndIf
			EndIf
					
		  	
			; saves the numbers of the vertices whose normalcoords have to be changed after updatenormals
			If x = 1
				vertstobechanged[i]=vertex[0]
				i = i + 1
				vertstobechanged[i]=vertex[1]
				i = i + 1
			ElseIf z = 1 
				vertstobechanged[i]=vertex[0]
				i = i + 1
				vertstobechanged[i]=vertex[3]
				i = i + 1
			ElseIf x = w\width-2 
				vertstobechanged[i]=vertex[2]
				i = i + 1
				vertstobechanged[i]=vertex[3]
				i = i + 1
			ElseIf z=w\depth-2
				vertstobechanged[i]=vertex[1]
				i = i + 1
				vertstobechanged[i]=vertex[2]
				i = i + 1
			EndIf
		Next 
	Next 
	
	UpdateNormals(w\mesh)
	
	; change some normalcoords to hide the tiling
	For i = 0 To 124*2
		VertexNormal w\sur,vertstobechanged[i],0,1,0
	Next
	
	BrushTexture w\brush,w\tex
	BrushTexture w\brush,w\tex2,Int(w\anim),2
	PaintSurface w\sur,w\brush
	
	w\anim = (w\anim + .5) Mod 23
	
	For s.wat_surf = Each wat_surf
		If s\w = w
			watdings = s\piv
			HideEntity s\piv
		EndIf
	Next
	
	; render the cubemap texture
	Wat_UpdateCubemap(w\tex,w\cam,wat_gamecam,watdings)
	
	For s.wat_surf = Each wat_surf
		If s\w = w
			ShowEntity s\piv
		EndIf
	Next
	
End Function


Function Wat_UpdateCubemap(tex,camera,entity,watdings) ; this function isnt by me. dont know who wrote it
   SetBuffer BackBuffer()				; nevertheless, thx to him for preventing me from havin' to write it on my own ^^
	tex_sz=TextureWidth(tex)
   ShowEntity camera
   PositionEntity env_sky,EntityX(camera,1),EntityY(camera,1),EntityZ(camera,1)
   HideEntity entity
   PositionEntity camera,EntityX#(entity,1),2*EntityY(watdings,1)-EntityY(entity,1),EntityZ#(entity,1),1
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

   ShowEntity entity
   HideEntity camera
   PositionEntity env_sky,EntityX(cc_cam,1),EntityY(cc_cam,1),EntityZ(cc_cam,1)
End Function