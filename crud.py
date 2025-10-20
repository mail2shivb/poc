# main_sql.py
from fastapi import FastAPI, HTTPException, Depends
from pydantic import BaseModel, Field
from typing import List, Optional
from sqlalchemy import Column, Integer, String, Float, create_engine
from sqlalchemy.orm import declarative_base, sessionmaker, Session

DATABASE_URL = "sqlite:///:memory:"  # in-memory
engine = create_engine(DATABASE_URL, connect_args={"check_same_thread": False})
SessionLocal = sessionmaker(bind=engine, autoflush=False, autocommit=False)
Base = declarative_base()

class ItemORM(Base):
    __tablename__ = "items"
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False)
    price = Column(Float, nullable=False)
    description = Column(String, nullable=True)

Base.metadata.create_all(bind=engine)

app = FastAPI(title="In-Memory SQLite CRUD")

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

class ItemCreate(BaseModel):
    name: str = Field(min_length=1)
    price: float = Field(ge=0)
    description: Optional[str] = None

class Item(BaseModel):
    id: int
    name: str
    price: float
    description: Optional[str] = None
    class Config:
        from_attributes = True  # Pydantic v2: map from ORM

@app.post("/items", response_model=Item, status_code=201)
def create_item(payload: ItemCreate, db: Session = Depends(get_db)):
    obj = ItemORM(**payload.model_dump())
    db.add(obj)
    db.commit()
    db.refresh(obj)
    return obj

@app.get("/items", response_model=List[Item])
def list_items(db: Session = Depends(get_db)):
    return db.query(ItemORM).all()

@app.get("/items/{item_id}", response_model=Item)
def get_item(item_id: int, db: Session = Depends(get_db)):
    obj = db.query(ItemORM).get(item_id)
    if not obj:
        raise HTTPException(status_code=404, detail="Item not found")
    return obj

@app.put("/items/{item_id}", response_model=Item)
def update_item(item_id: int, payload: ItemCreate, db: Session = Depends(get_db)):
    obj = db.query(ItemORM).get(item_id)
    if not obj:
        raise HTTPException(status_code=404, detail="Item not found")
    for k, v in payload.model_dump().items():
        setattr(obj, k, v)
    db.commit()
    db.refresh(obj)
    return obj

@app.delete("/items/{item_id}", status_code=204)
def delete_item(item_id: int, db: Session = Depends(get_db)):
    obj = db.query(ItemORM).get(item_id)
    if not obj:
        raise HTTPException(status_code=404, detail="Item not found")
    db.delete(obj)
    db.commit()
    return None
