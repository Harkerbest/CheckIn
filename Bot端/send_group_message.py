import requests

url = "http://服务器IP：端口号/v1/LuaApiCaller?funcname=MagicCgiCmd&timeout=10&qq=机器人QQ号"

def send(text,at_user = False,at_name = None):
   payload_direct = {
     "CgiCmd": "MessageSvc.PbSendMsg",
     "CgiRequest": {
       "ToUin" : 群号 ,
       "ToType": 2,
       "Content": text,
     }
   }

   payload_at = {
     "CgiCmd": "MessageSvc.PbSendMsg",
     "CgiRequest": {
       "ToUin" : 群号 ,
       "ToType": 2,
       "Content": text,
       "AtUinLists": [
         {
           "Nick": at_name,
           "Uin": at_user
         },
       ]
     }
   }

   if at_user:
      payload = payload_at
   else:
      payload = payload_direct
   response = requests.request("POST", url, json=payload)

   #print(response.text)
