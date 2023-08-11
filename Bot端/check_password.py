import mysql.connector


def check(password):
    try:
        # 连接数据库
        conn = mysql.connector.connect(
            host="数据库地址",
            user="数据库用户名",
            password="数据库密码",
            database="数据库名"
        )

        # 检查连接是否成功
        if conn.is_connected():
            # 创建游标对象
            cursor = conn.cursor()

            # 查询密码是否存在于数据库中
            query = "SELECT * FROM users WHERE password = %s"
            cursor.execute(query, (password,))
            result = cursor.fetchone()

            if result:
                # 删除数据库中的密码
                delete_query = "DELETE FROM users WHERE password = %s"
                cursor.execute(delete_query, (password,))
                conn.commit()

                # 关闭游标和数据库连接
                cursor.close()
                conn.close()

                return True
            else:
                # 关闭游标和数据库连接
                cursor.close()
                conn.close()

                return False
    except:
        return '数据库连接异常'
