package enterprise.headers
import enterprise.model.HeaderKey.Companion
import enterprise.model.Header.Constructor


final case class UserAgent(value: String) extends Constructor[UserAgent](UserAgent):
  override def renderValue: String = value


case object UserAgent extends Companion[UserAgent]:
  override def parseValue(text: String) = UserAgent(text)
