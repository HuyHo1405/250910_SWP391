from flask import Flask, request, jsonify
import google.generativeai as genai
import chromadb
import sys

app = Flask(__name__)

# --- CONFIG ---
API_KEY = "API-KEY".strip()

# Khởi tạo Model và DB một lần duy nhất khi chạy server
try:
    genai.configure(api_key=API_KEY)
    model = genai.GenerativeModel('gemini-2.5-flash')
    client = chromadb.PersistentClient(path="./vinfast_db")
    collection = client.get_collection(name="vinfast_services")
    print("✅ AI Server đã sẵn sàng tại cổng 5000!")
except Exception as e:
    print(f"❌ Lỗi khởi tạo: {e}")
    sys.exit()

@app.route('/chat', methods=['POST'])
def chat_endpoint():
    try:
        # 1. Nhận dữ liệu từ Spring Boot gửi sang
        data = request.json
        user_query = data.get('message', '')

        if not user_query:
            return jsonify({"reply": "Vui lòng nhập câu hỏi!"})

        # 2. Tìm kiếm trong DB (RAG)
        query_emb = genai.embed_content(
            model="models/text-embedding-004",
            content=user_query,
            task_type="retrieval_query"
        )['embedding']
        
        results = collection.query(
            query_embeddings=[query_emb],
            n_results=10
        )
        
        # 3. Nếu không tìm thấy gì
        if not results['documents'] or len(results['documents'][0]) == 0:
            return jsonify({"reply": "Xin lỗi, tôi không tìm thấy thông tin trong dữ liệu."})

        found_data = "\n".join(results['documents'][0])

        # 4. Gửi cho Gemini
        prompt = f"""
        Bạn là trợ lý ảo VinFast. Dựa vào dữ liệu sau để trả lời:
        DỮ LIỆU: {found_data}
        CÂU HỎI: {user_query}
        YÊU CẦU: Trả lời ngắn gọn, thân thiện.
        """
        
        response = model.generate_content(prompt)
        
        # 5. Trả kết quả về cho Spring Boot (dạng JSON)
        return jsonify({"reply": response.text})

    except Exception as e:
        return jsonify({"reply": f"Lỗi server: {str(e)}"})

if __name__ == '__main__':
    # Chạy server ở cổng 5000
    app.run(host='0.0.0.0', port=5000)