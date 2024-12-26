package enterprise.server
import turbolift.effects.Reader


type ConfigEffect = ConfigEffect.type

case object ConfigEffect extends Reader[Config]
