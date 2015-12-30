;      _________________________________________________________________________________________ 
;    //¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯\\
;   ||  ooooo  oooo ooooooooo  oooooooooo    ooooo       oo    ooooooo8 ooooo ooooo ooooooooooo  ||
;   ||   888    88   888    88o 888    888    888       8888 o888    88  888   888  88  888  88  ||
;   ||   888    88   888    888 888oooo88     888       8888 888    oooo 888ooo888      888      ||
;   ||   888    88   888    888 888           888      o 88  888o    88  888   888      888      ||
;   ||    888oo88   o888ooo88  o888o         o888ooooo88 oo   888ooo888 o888o o888o    o888o     ||
;   ||                                                                                           ||
;   ||         ------------------------------------------------------------------------          ||
;   ||       ooooo       ooooo oooooooooo oooooooooo       o      oooooooooo ooooo  oooo         ||
;   ||        888         888   888    888 888    888     888      888    888  888  88           ||
;   ||        888         888   888oooo88  888oooo88     8  88     888oooo88     888             ||
;   ||        888      o  888   888    888 888  88o     8oooo88    888  88o      888             ||
;   ||       o888ooooo88 o888o o888ooo888 o888o  88o8 o88o  o888o o888o  88o8   o888o            ||
;    \\_________________________________________________________________________________________//
;      ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯\ (c) 2003 by CodeMaster /¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ 
;                                      ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯


; #### COMMUNICATION COMMANDS : OUTPUT ##################
                                                       ;#
Const  UDP_Error              =  0                     ;#
Const  UDP_CheckPresence      =  1                     ;#
Const  UDP_Confirm            =  2                     ;#
Const  UDP_LogIn              =  3                     ;#
Const  UDP_StartDataTransfer  =  4                     ;#
Const  UDP_FinishDataTransfer =  5                     ;#
Const  UDP_Leave			  =  6
Const  UDP_AskFor			  =  7
Const  UDP_PingServer		  =  8


; #### ERROR CONSTANTS ##################################
                                                       ;#
Const  UDP_UnknownError       =  1                     ;#
Const  UDP_NetworkError       =  2                     ;#
Const  UDP_TimeOut            =  3                     ;#
Const  UDP_NameAlreadyExists  =  4                     ;#
Const  UDP_LoginError         =  5                     ;#
Const  UDP_ServerFull         =  6                     ;#
Const  UDP_NoConnection       =  7                     ;#
Const  UDP_TransferError      =  8                     ;#
Const  UDP_Kick			      =  9                     ;#


; #### INTERN MESSAGE CONSTANTS #########################
                                                       ;#
Const  UDP_ConSuccess         = 10                     ;#
Const  UDP_NewUser            = 11                     ;#
Const  UDP_IncomingData       = 12                     ;#
Const  UDP_UserLeft		      = 13                     ;#

; #### DATA TRANSFER IDENTIFIERS ########################
                                                       ;#
Const  UDP_Byte               =  1                     ;#
Const  UDP_Integer            =  2                     ;#
Const  UDP_String             =  3                     ;#
Const  UDP_Float              =  4                     ;#
Const  UDP_Short              =  5                     ;#

; #### RUNTIME CONSTANTS AND VARIABLES ##################
                                                       ;#
Const  UDP_Frequence          =  1                     ;#
Global UDP_TimeOutLength      = 10                     ;#
Global UDP_MaxNameLength      = 20                     ;#
Global UDP_Stream                                      ;#
Global UDP_TimeID                                      ;#
Global UDP_ID                                          ;#
Global UDP_Connected                                   ;#
Global UDP_IsServer                                    ;#
Global UDP_DataInQueue                                 ;#
Global UDP_TempFreq                                    ;#
Global UDP_Port, UDP_IP, UDP_Name$                     ;#
Global UDP_MaxUserCount       =  8                     ;#
Global UDP_UserCount                                   ;#
Global UDP_DataBankSize       =  10000                 ;#
Global UDP_DataBank[3]                                 ;#
Global UDP_DataBankPosition[3]                         ;#
Global UDP_DataStream=0
Global UDP_NextUDPDataType                             ;#

; #### SERVER VARIABLES AND TYPES #######################
                                                       ;#
Global UDP_Server_IP                                   ;#
Global UDP_Server_Port                                 ;#
Global UDP_Server_Name$                                ;#
Type UDP_User                                          ;#
 Field ID
 Field Name$                                           ;#
 Field IP                                              ;#
 Field Port                                            ;#
 Field TimeID                                          ;#
 Field PingTime                                        ;#
 Field Ship.Ship
 Field PCount
 Field ready
 Field LastP
End Type                                               ;#

Type Packet
 Field ID
 Field UserID
 Field bank
 Field Time
End Type

Dim UDP_Users.UDP_User(100)
Global UDP_UCount = 0

; #### CLIENT VARIABLES #################################
                                                       ;#
Global UDP_TempPingTime                                ;#
Global UDP_Server_PingTime                             ;#
Global UDP_SPCount
Global UDP_LastP

Type MissingPacket
	Field ID
	Field FromID
	Field Time
	Field AskedFor
End Type

; #### SERVER FUNCTIONS #################################
                                                       ;#
Function CreateUDPServer(Port=0,Name$="Server")        ;#
 UDP_Initialize
 If Port Then UDP_Stream = CreateUDPStream(Port) Else UDP_Stream = CreateUDPStream()
 If Not UDP_Stream Then UDP_HandleGameStatus(UDP_NetworkError) : Return
 UDP_Connected = 1
 UDP_Name$ = Trim$(Left$(Name$,UDP_MaxNameLength))
 UDP_IP = HostIP(CountHostIPs(""))
 UDP_Port = UDPStreamPort(UDP_Stream)
 UDP_TimeID = MilliSecs()
 UDP_ID = 255
 UDP_IsServer = True
 EraseUDPUser
 Return True
End Function

Function EraseUDPUser(UDP_UserID=0)
 If Not UDP_UserID Then
  UDP_UserCount = 0
  For UDP_TempUser.UDP_User = Each UDP_User
   UDP_Users(UDP_TempUser\id) = Null
   Delete UDP_TempUser
  Next
 Else
  UDP_UserCount = UDP_UserCount - 1
  UDP_TempUser.UDP_User = UDP_Users(UDP_UserID)
  UDP_Users(UDP_UserID) = Null
  s.ship = Null
  s.ship = UDP_TempUser\ship
  Delete UDP_TempUser
  If s.ship <> Null Then Shi_DeleteShip(s)
 EndIf
End Function

Function UDP_HandleServer()
 If UDP_Connected Then
  If MilliSecs() - UDP_TempFreq > UDP_Frequence * 10
   UDP_TempFreq = MilliSecs()
   While RecvUDPMsg(UDP_Stream)
    If ReadAvail(UDP_Stream)
     UDP_Action = ReadByte(UDP_Stream)
     Select UDP_Action
      Case UDP_LogIn 
  	   UDP_AcceptNewUsers 
	  Case UDP_CheckPresence
	   UDP_ConfirmPing
	  Case UDP_StartDataTransfer
	   TempID = ReadByte(UDP_Stream)
		TempNum = ReadInt(UDP_Stream)
	   If UDP_IDExists(TempID) Then
	    UDP_DataTransferActive = 1
		If TempNum > UDP_Users(TempID)\lastp+1
			For i = UDP_Users(TempID)\lastp+1 To TempNum-1
				m.MissingPacket = New MissingPacket
				m\id	 = i
				m\fromid = TempID
				m\time	 = MilliSecs()
			Next
		ElseIf TempNum < UDP_Users(TempID)\lastp
			For m.MissingPacket = Each MissingPacket
				If m\id = TempNum And m\FromID = TempID
					Delete m
				EndIf
			Next
		EndIf
		If TempNum > UDP_Users(TempID)\lastp Then UDP_Users(TempID)\lastp = TempNum
		UDP_HandleGameStatus(UDP_IncomingData,TempID,UDP_Users(TempID)\name)
	   EndIf
	  Case UDP_Leave
		TempID = ReadByte(UDP_Stream)
		If UDP_IDExists(TempID) Then
			UDP_TempUser.UDP_User = UDP_Users(TempID)
			If UDP_TempUser <> Null
				UDP_HandleGameStatus(UDP_UserLeft,TempID,UDP_TempUser\name)
				EraseUDPUser(TempID)
			EndIf
		EndIf
	   Case UDP_AskFor
			TempID = ReadByte(UDP_Stream)
			TempNum = ReadInt(UDP_Stream)
			UDP_TempUser.UDP_User = UDP_Users(TempID)
			If UDP_TempUser <> Null Then 
				For p.Packet = Each Packet
					If p\id = TempNum And TempNum = p\UserID
						WriteByte UDP_Stream,UDP_StartDataTransfer
						WriteByte UDP_Stream,UDP_ID
						WriteInt UDP_Stream,p\id
						WriteBytes p\bank,UDP_Stream,0,BankSize(p\bank)
	 					SendUDPMsg UDP_Stream,UDP_TempUser\IP,UDP_TempUser\Port
						FreeBank p\bank
						Delete p
					EndIf
				Next
			EndIf
		Case UDP_PingServer
			UDP_ServerAnswer
     End Select
    EndIf
   Wend
   ;If UDP_DataBankPosition Then SendUDPData
  EndIf
	For m.MissingPacket = Each MissingPacket
		If UDP_Users(m\fromid) = Null
			Delete m
		Else
			If MilliSecs()-m\time > 100 And m\askedfor = 0
				WriteByte UDP_Stream, UDP_AskFor
				WriteByte UDP_Stream, UDP_ID
				WriteInt UDP_Stream, m\id
				SendUDPMsg UDP_Stream,UDP_Users(m\fromid)\IP,UDP_Users(m\fromid)\Port
				m\askedfor = 1
			ElseIf MilliSecs()-m\time > 1500 And m\askedfor = 1
				Delete m
			EndIf
		EndIf
	Next
	For p.Packet = Each Packet
		If MilliSecs()-p\time > 4000
			FreeBank p\bank
			Delete p
		EndIf
	Next
  If MilliSecs() - UDP_TempPingTime > 1000
   UDP_TempPingTime = MilliSecs()
   For UDP_TempUser.UDP_User = Each UDP_User
	DebugLog "ping"
    WriteByte UDP_Stream,UDP_CheckPresence
    SendUDPMsg UDP_Stream,UDP_TempUser\IP,UDP_TempUser\Port
    UDP_TempTimeOut = (MilliSecs()-UDP_TempUser\PingTime)/100 Mod (UDP_TimeOutLength*10+5)
    If UDP_TempTimeOut > UDP_TimeOutLength * 10 Then
     UDP_UserCount = UDP_UserCount - 1
     UDP_HandleGameStatus(UDP_TimeOut,UDP_TempUser\id,UDP_TempUser\Name$,UDP_TempUser\IP,UDP_TempUser\Port)
     s.ship = Null
 	 s.ship = UDP_TempUser\ship  
     UDP_Users(UDP_TempUser\ID) = Null
     Delete UDP_TempUser
     If s.ship <> Null Then Shi_DeleteShip(s)
    EndIf
   Next
  EndIf
 EndIf
End Function

Function UDP_AcceptNewUsers()
 UDP_UserAlreadyExists = 0
 UDP_NameAlreadyRegistered = 0
 UDP_Output_Action = UDP_Confirm
 UDP_Input_TimeID = ReadInt(UDP_Stream)
 UDP_Input_Name$ = ReadString(UDP_Stream)
 If Trim$(Lower$(UDP_Input_Name$)) = Trim$(Lower$(UDP_Name$)) Then UDP_NameAlreadyRegistered = 1
 For UDP_TempUser.UDP_User = Each UDP_User
  If UDP_Input_TimeID = UDP_TempUser\TimeID Then UDP_UserAlreadyExists = 1
  If Trim$(Lower$(UDP_Input_Name$)) = Trim$(Lower$(UDP_TempUser\Name$)) Then UDP_NameAlreadyRegistered = 1
 Next
 UDP_Input_IP = UDPMsgIP(UDP_Stream)
 UDP_Input_Port = UDPMsgPort(UDP_Stream)
 If Not UDP_UserAlreadyExists Then
  If Not UDP_NameAlreadyRegistered Then
   UDP_Output_ID = CreateUDPUser(UDP_Input_Name$,UDP_Input_IP,UDP_Input_Port,UDP_Input_TimeID)
   Select UDP_Output_ID
    Case UDP_Error
     UDP_Output_Action = UDP_Error
     UDP_ErrorID = UDP_LoginError
    Case -1
     UDP_Output_Action = UDP_Error
     UDP_ErrorID = UDP_ServerFull
   End Select
  Else
   UDP_Output_Action = UDP_Error
   UDP_ErrorID = UDP_NameAlreadyExists
  EndIf
 EndIf
 WriteByte UDP_Stream,UDP_Output_Action
 WriteInt UDP_Stream,UDP_Input_TimeID
 Select UDP_Output_Action
  Case UDP_Confirm
   WriteByte UDP_Stream,UDP_Output_ID
   WriteString UDP_Stream,UDP_Name$
	WriteString UDP_Stream,main_map
	DebugLog "new user "+UDP_Output_ID
  Case UDP_Error
   WriteByte UDP_Stream,UDP_ErrorID
 End Select
 SendUDPMsg UDP_Stream,UDP_Input_IP,UDP_Input_Port
	If UDP_Confirm = UDP_Output_Action
		;Net_SSynchronize(UDP_Output_ID)
	EndIf
End Function

Function UDP_ServerAnswer()
	UDP_Input_IP = UDPMsgIP(UDP_Stream)
	UDP_Input_Port = UDPMsgPort(UDP_Stream)
	WriteByte UDP_Stream,UDP_PingServer
	WriteByte UDP_Stream,UDP_MaxUserCount+1
	WriteByte UDP_Stream, UDP_UserCount+1
	WriteString UDP_Stream,main_map
	SendUDPMsg UDP_Stream,UDP_Input_IP,UDP_Input_Port
End Function

Function UDP_ConfirmPing()
 UDP_Input_ID = ReadByte(UDP_Stream)
 If UDP_IDExists(UDP_Input_ID) Then
	DebugLog "pong "+UDP_Input_ID
  UDP_TempUser.UDP_User = UDP_Users(UDP_Input_ID)
  UDP_TempUser\PingTime = MilliSecs()
Else
	DebugLog "unknown pong?"+UDP_Input_ID
 EndIf
End Function

Function UDP_IDExists(UDP_TempID)
 For UDP_TempI.UDP_User = Each UDP_User
  If UDP_TempI\id = UDP_TempID Then Return True
 Next
 Return False
End Function

Function CreateUDPUser(Name$,IP,Port,TimeID)
 If UDP_UserCount < UDP_MaxUserCount Then
  UDP_UserCount = UDP_UserCount + 1
  UDP_UCount = UDP_UCount + 1
  NewUDPUser.UDP_User = New UDP_User
  NewUDPUser\ID = UDP_UCount
  UDP_Users(UDP_UCount) = NewUDPUser
   NewUDPUser\Name$ = Name$
   NewUDPUser\IP = IP
   NewUDPUser\Port = Port
   NewUDPUser\TimeID = TimeID
   NewUDPUser\PingTime = MilliSecs()
  If NewUDPUser\id>0 Then
   UDP_HandleGameStatus(UDP_NewUser,NewUDPUser\id,Name$,IP,Port)
   Return NewUDPUser\id
  Else
   UDP_HandleGameStatus(UDP_LoginError,0,Name$,IP,Port)
   Return UDP_Error
  EndIf
 Else
  Return -1
 EndIf
End Function

Function UDP_KickUser(UserID, Reason$="")
	UDP_TempUser.UDP_User = UDP_Users(UserID)
	WriteByte UDP_Stream,UDP_Kick
	WriteString UDP_Stream,Reason
	SendUDPMsg UDP_Stream,UDP_TempUser\IP,UDP_TempUser\Port
	HUD_PrintLog UDP_TempUser\name+ " wird gekickt! "+Reason$,255,255,0
	EraseUDPUser(UserID)
End Function


; #### CLIENT FUNCTIONS #################################
                                                       ;#
Function JoinUDPGame(IP$,Port,Name$,UserPort=0)        ;#
 UDP_Initialize
 If UserPort Then UDP_Stream = CreateUDPStream(UserPort) Else UDP_Stream = CreateUDPStream()
 If Not UDP_Stream Then UDP_HandleGameStatus(UDP_NetworkError) : Return
 UDP_Server_IP   = UDP_IPint(IP$)
 UDP_Server_Port = Port
 UDP_Name$       = Trim$(Left$(Name$,UDP_MaxNameLength))
 UDP_IP          = HostIP(CountHostIPs(""))
 UDP_Port        = UDPStreamPort(UDP_Stream)
 UDP_TimeID      = MilliSecs()
 UDP_Server_PingTime = MilliSecs()
 While (MilliSecs() - UDP_TimeID < UDP_TimeOutLength * 1000) And (Not KeyDown(1))
  If MilliSecs() - UDP_TempFreq > UDP_Frequence
   UDP_TempFreq = MilliSecs()
   While RecvUDPMsg(UDP_Stream)
    If ReadAvail(UDP_Stream) Then
     UDP_Action = ReadByte(UDP_Stream)
	 Select UDP_Action
      Case UDP_Confirm
       UDP_Recv_TimeID = ReadInt(UDP_Stream)
       UDP_Recv_ID = ReadByte(UDP_Stream)
	   UDP_Server_Name$ = ReadString(UDP_Stream)
		Main_map = ReadString(UDP_Stream)
       If UDP_Recv_TimeID = UDP_TimeID Then UDP_Connected = True : UDP_ID = UDP_Recv_ID : DebugLog "got id "+UDP_Recv_ID : Return UDP_Recv_ID
	  Case UDP_Error
	   UDP_Recv_TimeID = ReadInt(UDP_Stream)
	   UDP_ErrorID = ReadByte(UDP_Stream)
	   If UDP_Recv_TimeID = UDP_TimeID Then UDP_HandleGameStatus(UDP_ErrorID) : Return 0
     End Select
    EndIf
   Wend
   WriteByte UDP_Stream,UDP_LogIn
   WriteInt UDP_Stream,UDP_TimeID
   WriteString UDP_Stream,UDP_Name$
   SendUDPMsg UDP_Stream,UDP_Server_IP,UDP_Server_Port
  EndIf
 Wend
 UDP_HandleGameStatus(UDP_TimeOut)
End Function

Function UDP_PingAServer(IP$,Port)
	Server_IP   = UDP_IPint(IP$)
	WriteByte menu_udpstream, UDP_PingServer
	SendUDPMsg menu_udpstream, Server_IP, port
End Function

Function UDP_HandleClient()
 If UDP_Connected Then
  If MilliSecs() - UDP_TempFreq > UDP_Frequence * 10
   UDP_TempFreq = MilliSecs()
   While RecvUDPMsg(UDP_Stream)
    If ReadAvail(UDP_Stream)
     UDP_Action = ReadByte(UDP_Stream)
     Select UDP_Action
      Case UDP_CheckPresence
       UDP_Server_PingTime = MilliSecs()
		DebugLog "pong"
      Case UDP_Kick
        UDP_HandleGameStatus(UDP_Kick)
		Exit
      Case UDP_StartDataTransfer
		TempID = ReadByte(UDP_Stream)
		TempNum = ReadInt(UDP_Stream)
		If TempID = 255
			If TempNum > UDP_LastP+1
				For i = UDP_LastP To TempNum-1
					m.MissingPacket = New MissingPacket
					m\id	 = i
					m\fromid = TempID
					m\time	 = MilliSecs()
				Next
			ElseIf TempNum < UDP_LastP
				For m.MissingPacket = Each MissingPacket
					If m\id = TempNum And m\FromID = TempID
						Delete m
					EndIf
				Next
			EndIf
			If TempNum > UDP_LastP Then UDP_LastP = TempNum
	        UDP_DataTransferActive = 1
      		UDP_HandleGameStatus(UDP_IncomingData)
       EndIf
		Case UDP_AskFor
			TempID = ReadByte(UDP_Stream)
			TempNum = ReadInt(UDP_Stream)
			For p.Packet = Each Packet
				If p\id = TempNum And TempNum = p\UserID
					WriteByte UDP_Stream,UDP_StartDataTransfer
					WriteByte UDP_Stream,UDP_ID
					WriteInt UDP_Stream,p\id
					WriteBytes p\bank,UDP_Stream,0,BankSize(p\bank)
 					SendUDPMsg UDP_Stream,UDP_Server_IP,UDP_Server_Port
					FreeBank p\bank
					Delete p
				EndIf
			Next
     End Select
    EndIf
   Wend
   ;If UDP_DataBankPosition Then SendUDPData
  EndIf
	For m.MissingPacket = Each MissingPacket
		If m\fromid <> 255
			Delete m
		Else
			If MilliSecs()-m\time > 100 And m\askedfor = 0
				WriteByte UDP_Stream, UDP_AskFor
				WriteByte UDP_Stream, UDP_ID
				WriteInt UDP_Stream, m\id
				SendUDPMsg UDP_Stream,UDP_Server_IP,UDP_Server_Port
				m\askedfor = 1
			ElseIf MilliSecs()-m\time > 1500 And m\askedfor = 1
				Delete m
			EndIf
		EndIf
	Next
	For p.Packet = Each Packet
		If MilliSecs()-p\time > 4000
			FreeBank p\bank
			Delete p
		EndIf
	Next
  If MilliSecs() - UDP_TempPingTime > 1000
   UDP_TempPingTime = MilliSecs()
   WriteByte UDP_Stream,UDP_CheckPresence
   WriteByte UDP_Stream,UDP_ID
	DebugLog "ping"
   SendUDPMsg UDP_Stream,UDP_Server_IP,UDP_Server_Port
   If (MilliSecs()-UDP_Server_PingTime)/100 Mod (UDP_TimeOutLength*10+5) > UDP_TimeOutLength * 10 Then
    UDP_HandleGameStatus(UDP_TimeOut)
   EndIf
  EndIf
 EndIf
End Function

Function UDP_UserLeave()
	WriteByte UDP_Stream,UDP_Leave
	WriteByte  UDP_Stream,UDP_ID
	SendUDPMsg UDP_Stream,UDP_Server_IP,UDP_Server_Port
End Function

; #### RUNTIME FUNCTIONS ################################
                                                       ;#
SeedRnd MilliSecs()                                    ;#
Function UDP_Initialize()                              ;#
 UDP_IsServer = False
 UDP_Connected = False
 UDP_ID = False
 EraseUDPUser
 UDP_UCount = 0
 UDP_SPCount = 0
 UDP_LastP = 0
 For i = 0 To 3
  UDP_DataBank[i] = CreateBank(UDP_DataBankSize)
 Next
End Function

Function UDP_IPint(IP$)
 a1=Int(Left(IP$,Instr(IP$,".")-1)):IP$=Right(IP$,Len(IP$)-Instr(IP$,"."))
 a2=Int(Left(IP$,Instr(IP$,".")-1)):IP$=Right(IP$,Len(IP$)-Instr(IP$,"."))
 a3=Int(Left(IP$,Instr(IP$,".")-1)):IP$=Right(IP$,Len(IP$)-Instr(IP$,"."))
 a4=Int(IP$)
 Return (a1 Shl 24) + (a2 Shl 16) + (a3 Shl 8 ) +a4
End Function

Function QuitUDPGame()
 If UDP_Stream <> 0 Then CloseUDPStream UDP_Stream
 UDP_Stream = 0
 UDP_Connected = 0
End Function

Function AddUDPByte(TempByte)
 If UDP_DataBankPosition[UDP_DataStream] = 0 Then
  UDP_DataBankPosition[UDP_DataStream] = 2
 EndIf
 ;PokeByte UDP_DataBank,UDP_DataBankPosition,UDP_Byte
 PokeByte UDP_DataBank[UDP_DataStream],UDP_DataBankPosition[UDP_DataStream],TempByte
 UDP_DataBankPosition[UDP_DataStream] = UDP_DataBankPosition[UDP_DataStream]+1
End Function

Function AddUDPInteger(TempInteger)
 If UDP_DataBankPosition[UDP_DataStream] = 0 Then
  UDP_DataBankPosition[UDP_DataStream] = 2
 EndIf
 ;PokeByte UDP_DataBank,UDP_DataBankPosition,UDP_Integer
 PokeInt UDP_DataBank[UDP_DataStream],UDP_DataBankPosition[UDP_DataStream],TempInteger
 UDP_DataBankPosition[UDP_DataStream] = UDP_DataBankPosition[UDP_DataStream]+4
End Function

Function AddUDPString(TempString$)
 If UDP_DataBankPosition[UDP_DataStream] = 0 Then
  UDP_DataBankPosition[UDP_DataStream] = 2
 EndIf
 ;PokeByte UDP_DataBank,UDP_DataBankPosition,UDP_String
 TempString$ = Left$(TempString$,255)
 PokeByte UDP_DataBank[UDP_DataStream],UDP_DataBankPosition[UDP_DataStream],Len(TempString$)
 For i = 1 To Len(TempString$)
  PokeByte UDP_DataBank[UDP_DataStream],UDP_DataBankPosition[UDP_DataStream]+i,Asc(Mid$(TempString$,i,1))
 Next
 UDP_DataBankPosition[UDP_DataStream] = UDP_DataBankPosition[UDP_DataStream]+1+Len(TempString$)
End Function

Function AddUDPFloat(TempFloat#)
 If UDP_DataBankPosition[UDP_DataStream] = 0 Then
  UDP_DataBankPosition[UDP_DataStream] = 2
 EndIf
 ;PokeByte UDP_DataBank,UDP_DataBankPosition,UDP_Float
 PokeFloat UDP_DataBank[UDP_DataStream],UDP_DataBankPosition[UDP_DataStream],TempFloat
 UDP_DataBankPosition[UDP_DataStream] = UDP_DataBankPosition[UDP_DataStream]+4
End Function

Function AddUDPShort(TempShort)
 If UDP_DataBankPosition[UDP_DataStream] = 0 Then
  UDP_DataBankPosition[UDP_DataStream] = 2
 EndIf
 ;PokeByte UDP_DataBank,UDP_DataBankPosition,UDP_Short
 PokeShort UDP_DataBank[UDP_DataStream],UDP_DataBankPosition[UDP_DataStream],TempShort
 UDP_DataBankPosition[UDP_DataStream] = UDP_DataBankPosition[UDP_DataStream]+2
End Function

Function SendUDPData(ReceiverID=0)
 ;PokeByte UDP_DataBank[UDP_DataStream],0,UDP_StartDataTransfer
 ;PokeByte UDP_DataBank[UDP_DataStream],1,UDP_ID
 PokeByte UDP_DataBank[UDP_DataStream],UDP_DataBankPosition[UDP_DataStream],UDP_FinishDataTrasnfer
 UDP_DataBankPosition[UDP_DataStream] = UDP_DataBankPosition[UDP_DataStream] + 1
 If UDP_IsServer Then
  If ReceiverID And UDP_IDExists(ReceiverID) Then
   UDP_TempUser.UDP_User = UDP_Users(ReceiverID)
   p.Packet= New Packet
	p\Id	= UDP_TempUser\pcount
	p\UserID= UDP_TempUser\ID
	p\bank	= CreateBank(UDP_DataBankPosition[UDP_DataStream]-2)
	CopyBank UDP_DataBank[UDP_DataStream],2,p\bank,0,UDP_DataBankPosition[UDP_DataStream]-2
	p\time	= MilliSecs()
   WriteByte UDP_Stream,UDP_StartDataTransfer
   WriteByte UDP_Stream,UDP_ID
   WriteInt UDP_Stream,UDP_TempUser\pcount
   UDP_TempUser\pcount = UDP_TempUser\pcount + 1
   WriteBytes UDP_DataBank[UDP_DataStream],UDP_Stream,2,UDP_DataBankPosition[UDP_DataStream]-2
   SendUDPMsg UDP_Stream,UDP_TempUser\IP,UDP_TempUser\Port
   net_datasum = net_datasum + UDP_DataBankPosition[UDP_DataStream]
  Else
   For UDP_TempUser.UDP_User = Each UDP_User
	If UDP_TempUser\ready Then 
		p.Packet= New Packet
		p\Id	= UDP_TempUser\pcount
		p\UserID= UDP_TempUser\ID
		p\bank	= CreateBank(UDP_DataBankPosition[UDP_DataStream]-2)
		CopyBank UDP_DataBank[UDP_DataStream],2,p\bank,0,UDP_DataBankPosition[UDP_DataStream]-2
		p\time	= MilliSecs()
		WriteByte UDP_Stream,UDP_StartDataTransfer
	    WriteByte UDP_Stream,UDP_ID
	    WriteInt UDP_Stream,UDP_TempUser\pcount
	    UDP_TempUser\pcount = UDP_TempUser\pcount + 1
	    WriteBytes UDP_DataBank[UDP_DataStream],UDP_Stream,2,UDP_DataBankPosition[UDP_DataStream]-2
	    SendUDPMsg UDP_Stream,UDP_TempUser\IP,UDP_TempUser\Port
    	net_datasum = net_datasum + UDP_DataBankPosition[UDP_DataStream]
    EndIf
   Next
  EndIf
 Else
    p.Packet= New Packet
	p\Id	= UDP_SPCount
	p\UserID= 255
	p\bank	= CreateBank(UDP_DataBankPosition[UDP_DataStream]-2)
	CopyBank UDP_DataBank[UDP_DataStream],2,p\bank,0,UDP_DataBankPosition[UDP_DataStream]-2
	p\time	= MilliSecs()
  WriteByte UDP_Stream,UDP_StartDataTransfer
  WriteByte UDP_Stream,UDP_ID
  WriteInt UDP_Stream,UDP_SPCount
  UDP_SPCount = UDP_SPCount + 1
  WriteBytes UDP_DataBank[UDP_DataStream],UDP_Stream,2,UDP_DataBankPosition[UDP_DataStream]-2
  SendUDPMsg UDP_Stream,UDP_Server_IP,UDP_Server_Port
  net_datasum = net_datasum + UDP_DataBankPosition[UDP_DataStream]
 EndIf
 UDP_DataBankPosition[UDP_DataStream] = 0
 ;ClearUDPDataBank
End Function

Function ClearUDPDataBank()
 For i = 0 To UDP_DataBankSize-1
  PokeByte UDP_DataBank[UDP_DataStream],i,0
 Next
 UDP_DataBankPosition[UDP_DataStream] = 0
End Function

Function NextUDPType()
 Return ReadByte(UDP_Stream)
End Function

Function ReadUDPByte()
 ;If NextUDPType() = UDP_Byte Then
  TempByte = ReadByte(UDP_Stream)
  Return TempByte
 ;EndIf
End Function

Function ReadUDPInteger()
 ;If NextUDPType() = UDP_Integer Then
  TempInteger = ReadInt(UDP_Stream)
  Return TempInteger
 ;EndIf
End Function

Function ReadUDPString$()
 ;If NextUDPType() = UDP_String Then
  length = ReadByte(UDP_Stream)
  For i = 1 To length
   TempString$ = TempString$ + Chr$(ReadByte(UDP_Stream))
  Next
  Return TempString$
 ;EndIf
End Function

Function ReadUDPFloat#()
 ;If NextUDPType() = UDP_Float Then
  TempFloat# = ReadFloat(UDP_Stream)
  Return TempFloat#
 ;EndIf
End Function

Function ReadUDPShort()
 ;If NextUDPType() = UDP_Short Then
  TempShort = ReadShort(UDP_Stream)
  Return TempShort
 ;EndIf
End Function

Function GetUDPUserName$(UserID)
 UDP_TempUser.UDP_User = UDP_Users(UserID)
 Return UDP_TempUser\Name$
End Function