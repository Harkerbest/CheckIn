import requests
import json

def deal(MsgSeq,is_agree):
   url = "http://服务器IP：端口/v1/LuaApiCaller?funcname=MagicCgiCmd&timeout=10&qq=机器人QQ号"
   if is_agree == True:
      opcode = 1
   else:
      opcode = 2
   payload = json.dumps({
      "CgiCmd": "SystemMsgAction.Group",
      "CgiRequest": {
         "MsgSeq": MsgSeq,
         "MsgType": 1,
         "GroupCode": 群号,
         "OpCode": opcode #1为同意，2为拒绝，3为忽略请求
      }
   })

   response = requests.request("POST", url,  data=payload)
