import google.generativeai as genai
import chromadb
import os

# --- THAY KEY CỦA BẠN VÀO ĐÂY ---
API_KEY = "API-KEY" 

genai.configure(api_key=API_KEY)

def build_database():
    print("dang doc du lieu...")
    
    # --- THÊM ĐOẠN NÀY ĐỂ KIỂM TRA ---
    if not os.path.exists("vinfast_data.txt"):
        print("❌ LỖI: Không tìm thấy file vinfast_data.txt đâu cả!")
        return

    with open("vinfast_data.txt", "r", encoding="utf-8") as f:
        raw_text = f.read()
    
    print(f"🔍 Đã đọc được {len(raw_text)} ký tự từ file.") # <--- Kiểm tra xem có đọc được gì không

    documents = [line.strip() for line in raw_text.split('\n') if line.strip() and not line.startswith("#")]
    
    print(f"📊 Số dòng dữ liệu sạch tìm được: {len(documents)}") # <--- Nếu số này = 0 là lỗi
    
    if len(documents) == 0:
        print("❌ LỖI: File có dữ liệu nhưng code không lấy được dòng nào! Kiểm tra lại nội dung file.")
        returnx
    
    # Tạo DB
    print("dang nap vao nao...")
    client = chromadb.PersistentClient(path="./vinfast_db") # Lưu DB vào thư mục này
    
    try:
        client.delete_collection(name="vinfast_services")
    except:
        pass
        
    collection = client.create_collection(name="vinfast_services")

    # Embed dữ liệu (Biến chữ thành số)
    # Batch processing để chạy cho nhanh
    batch_size = 20
    for i in range(0, len(documents), batch_size):
        batch = documents[i : i + batch_size]
        ids = [str(j) for j in range(i, i + len(batch))]
        
        result = genai.embed_content(
            model="models/text-embedding-004",
            content=batch,
            task_type="retrieval_document"
        )
        
        collection.add(ids=ids, documents=batch, embeddings=result['embedding'])
        print(f"   Da nap xong lo {i}")

    print("✅ Xong! Bot da hoc thuoc bai.")

if __name__ == "__main__":
    build_database()