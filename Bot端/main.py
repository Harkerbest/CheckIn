import asyncio
import json
import random
import websockets
import deal_application
import send_group_message
import check_password
import sys
from loguru import logger

logger.remove(0)
logger.add(sys.stderr, level="INFO")
logger.add('bot-output.log')
#websocket client
SERCIVE_HOST = "服务器IP：端口"
async def Wsdemo():
    uri = "ws://{}/ws".format(SERCIVE_HOST)
    try:
        async with websockets.connect(uri) as websocket:
            while True:
                greeting = await websocket.recv()
                EventJson = json.loads(greeting)
                EventName = EventJson["CurrentPacket"]["EventName"]
                EventData = EventJson["CurrentPacket"]["EventData"]
                #print(f"<{EventName} {greeting}")
                if EventName == 'ON_EVENT_GROUP_SYSTEM_MSG_NOTIFY':
                    msg_type = EventData['MsgType'] # 1则为申请入群
                    status = EventData['Status'] # 1为未处理
                    MsgSeq=EventData['MsgSeq']
                    GroupCode=EventData['GroupCode'] # 获取群组qq号
                    applicant_name = EventData['ReqUidNick'] #申请人昵称
                    answer = EventData['MsgAdditional'].strip('问题：本群的密码是\n答案：')#申请信息，strip过滤掉答案外部分
                    if GroupCode == 群号 and msg_type == 1 and status == 1:#判断是否为指定群聊未处理的申请信息
                        logger.info('收到{}的进群申请，回答问题答案为：{}'.format(applicant_name,answer))
                        if check_password.check(answer):#返回True则密码验证成功
                            deal_application.deal(MsgSeq,True)
                            logger.success('已同意申请')
                            send_group_message.send('已通过来自{}的入群申请 \n 请新人爆电脑 or 数据中心配置\n 当然也可以吐槽群友出的入群测试'.format(applicant_name))
                        elif check_password.check(answer) == '数据库连接异常':
                            send_group_message.send('处理来自{}的入群申请时出现异常，请管理员手动处理'.format(applicant_name))
                            logger.error('处理来自{}的入群申请时出现异常'.format(applicant_name))
                        else:
                            deal_application.deal(MsgSeq,False)
                            logger.warning('已拒绝申请')
                elif EventName == 'ON_EVENT_GROUP_JOIN':#进群后的处理
                    GroupCode = EventData['MsgHead']['FromUin']
                    join_uin = EventData['MsgHead']['SenderUin']
                    join_name = EventData['MsgHead']['SenderNick']
                    #logger.info('捕获新人进群信息'+greeting)
                    # logger.info(f'发现新人入群，群号{GroupCode}，QQ号{join_uin}，昵称{join_name}')
                    # if GroupCode == 群号:
                    #     send_group_message.send('欢迎新人入群，欢迎分享您对入群试题的感想',join_uin,join_name)

    except Exception as e:
        # 断线重连
        t = random.randint(5, 8)
        logger.error(f"< 超时重连中... { t}"+ str(e))
        await asyncio.sleep(t)
        await Wsdemo()
asyncio.get_event_loop().run_until_complete(Wsdemo())
