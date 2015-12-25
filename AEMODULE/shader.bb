;ich nenne das einfach mal shader. das sind vorgefertigte effekte für faden, scalen, wasser usw...

Type shader
	Field name$
	Field entity
	Field updatetimer
	Field lastupdate
	
	Field speed#
	Field time#=1
	Field endtime
	Field bank
	;usw....
End Type

Function Sha_AddShader(entity,name$,updatetimer,speed#,endtime=0)
	sh.shader	= New shader
	sh\name		= name
	sh\entity	= entity
	sh\speed	= speed
	sh\endtime	= endtime
	sh\updatetimer = updatetimer / main_detail
	sh\lastupdate = MilliSecs()
	Select name
	Case "water"
		sh\bank = CreateBank(4)
		sh\endtime = GetSurface(entity,1)
		height# = MeshHeight(entity)/2
		sh\time = height/2
		For v = 0 To CountVertices(sh\endtime)-1
			y# = VertexY(sh\endtime,v) / height
			PokeFloat sh\bank,point,ASin(y)*2+Rand(-30,90)
			point = point + 4
			ResizeBank sh\bank,point+4
		Next
	End Select
End Function

Function Sha_Update()
	For sh.shader = Each shader
		Select sh\name
		Case "scaleup"
			If sh\updatetimer + sh\lastupdate < MilliSecs()
				sh\time = sh\time + sh\updatetimer * sh\speed
				ScaleEntity sh\entity,sh\time,sh\time,sh\time
				sh\endtime = sh\endtime - sh\updatetimer
				If sh\endtime <= 0 Then Delete sh.shader
			EndIf
			
		Case "fadehide"
			If sh\updatetimer + sh\lastupdate < MilliSecs()
				If EntityVisible(cam_cam,sh\entity)
					sh\endtime = 1
				Else
					sh\endtime = 0
				EndIf
				sh\lastupdate = MilliSecs()
			EndIf
			If sh\time < sh\endtime Then sh\time = sh\time + sh\speed * main_gspe
			If sh\time > sh\endtime Then sh\time = sh\time - sh\speed * main_gspe
			EntityAlpha sh\entity,sh\time#
		Case "water"
			If sh\updatetimer + sh\lastupdate < MilliSecs()
				If EntityInView(sh\entity,cam_cam)
					For v = 0 To CountVertices(sh\endtime)-1
						yw# = PeekFloat(sh\bank,point)
						yw# = (yw + sh\speed*sh\updatetimer) Mod 360 ;sh\updatetimer * sh\speed
						PokeFloat sh\bank,point,yw
						point = point + 4
						
						VertexCoords sh\endtime,v,VertexX(sh\endtime,v),Sin(yw)*sh\time,VertexZ(sh\endtime,v)
					Next
					If main_detail >= 1 UpdateNormals sh\entity
				EndIf
				sh\lastupdate = MilliSecs()
				
			EndIf
		End Select
	Next
End Function

Function Sha_FreeShader(entity)
	For sh.shader = Each shader
		If entity = sh\entity
			Delete sh.shader
		End If
	Next
End Function

Function Sha_Clear()
	For sh.shader = Each shader
		Delete sh.shader
	Next
End Function