package enterprise.server
import turbolift.effects.ReaderEffect


type ConfigEffect = ConfigEffect.type

case object ConfigEffect extends ReaderEffect[Config]
