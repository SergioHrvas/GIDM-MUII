from enum import Enum

class StatusEnum(str, Enum):
    pending = "pending"
    approved = "approved"
    rejected = "rejected"
