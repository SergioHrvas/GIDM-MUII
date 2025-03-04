from enum import Enum

class StatusEnum(str, Enum):
    pending = "pending"
    finished = "finished"
    rejected = "rejected"
