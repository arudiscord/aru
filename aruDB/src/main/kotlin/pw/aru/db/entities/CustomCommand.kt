package pw.aru.db.entities

import pw.aru.db.AruDB
import pw.aru.db.base.RedisField
import pw.aru.db.base.RedisObject

class CustomCommand(db: AruDB, id: Long) : RedisObject(db, id) {
    //var permission: CommandPermission by RedisField(Serializer.enum(), CommandPermission.USER)

    var value: String by RedisField.String()
}