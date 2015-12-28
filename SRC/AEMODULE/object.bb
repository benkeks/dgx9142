Const TLAY_DOT3	= 0
Const TLAY_COLOMAP	= 1
Const TLAY_CUBEMAP	= 5

Const OBJ_MESH		= 41
Const OBJ_TRIGGER	= 51
Const OBJ_EFFECT	= 61
Const OBJ_LEVELSTART= 71
Const OBJ_LEVELEND	= 81
Const OBJ_FLARELIGHT= 91
Const OBJ_ITEM		= 101

Const NO_Collisions		= 0
Const BLITZ3D_Collisions	= 1
Const TOKA_Collisions	= 2

Const BOX_Collisions		= 0
Const RADIUS_Collisions		= 1
Const CYLINDER_Collisions	= 2
Const STATIC_Collisions		= 3

Type obj
	Field TYP		; wird mit ner Const gefüllt
	
	Field entity

	Field Toka
	Field mass#
	
	Field name$
	Field material
	Field collEngine

	Field px#,py#,pz#
	Field rx#,ry#,rz#

	Field TokTyp

	Field materialClass
	
	Field hp ; hitpoints
	
	; CubicStuff
	Field cub_rmode			; RenderMode	> Consts "RMOD_~"
	Field cub_size			; TextureSize
	Field cub_Tex			; HANDLE der Texture

	; Dot3Stuff
	Field dot3_tex
	Field dot3_static
End Type

Global countObjs
Function Obj_New.obj()

	countObjs = countObjs +1
	o.obj = New obj
	Return o

End Function

Function Obj_SetObj( o.obj,mesh,name$)

	o\entity	= mesh
	o\name$	= name
	NameEntity o\entity,o\name$

	o\px	= EntityX(o\entity)
	o\py	= EntityY(o\entity)
	o\pz	= EntityZ(o\entity)
	o\rx	= EntityPitch(o\entity)
	o\ry	= EntityYaw(o\entity)
	o\rz	= EntityRoll(o\entity)
	
End Function

Function Obj_Update()

	For o.obj = Each obj
		;If o\collEngine	= TOKA_Collisions Then Toklib_UpdateObj( o )
		Cub_UpdateMap( o )
		If o\entity Then Dot3_UpdateDot3( o )
	Next
	
End Function

Function Obj_GetObject.obj( entity )

	For o.obj = Each obj
		If o\entity = entity Then
			Return o.obj
		EndIf
	Next
End Function

Function Obj_FindObject.obj( name$ )

	For o.obj = Each obj
		If o\name = name Then
			Return o.obj
		EndIf
	Next
End Function

Function Obj_Clear()
	For o.obj = Each obj
		If o\cub_tex Then FreeTexture o\cub_tex
		FreeEntity o\entity
		Delete o.obj
	Next
End Function