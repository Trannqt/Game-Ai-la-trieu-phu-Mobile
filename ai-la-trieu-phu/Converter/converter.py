# Mở text file và tách dòng
textFile = open("note.txt", "r", encoding="utf-8")
lines = textFile.read().split("\n")
textFile.close()

# Tạo file json
jsonFile = open("firebaseData.json", "w+", encoding="utf-8")

# Viết câu hỏi và câu trả lời
jsonFile.write("{\n    \"Questions\" : {\n")
for line in lines:
    # Tách từng dòng ra thành từng chuỗi
    strls = line.split("#")
    # Chuỗi có ! được lưu thành answer sau đó xóa dấu !
    for str in strls:
        if(str[0] == "!"):
            answer = str.replace("!", "", 1)
            strls[strls.index(str)] = answer
    jsonFile.write(f"       \"{lines.index(line)}\" : ")
    jsonFile.write("{\n")
    jsonFile.write(f"           \"question\" : \"{strls[0]}\",\n")
    jsonFile.write(f"           \"option1\"  : \"{strls[1]}\",\n")
    jsonFile.write(f"           \"option2\"  : \"{strls[2]}\",\n")
    jsonFile.write(f"           \"option3\"  : \"{strls[3]}\",\n")
    jsonFile.write(f"           \"option4\"  : \"{strls[-1]}\",\n")
    jsonFile.write(f"           \"answer\"   : \"{answer}\"\n")
    # Nếu là dòng cuối không viết dấu ,
    if(line != lines[-1]):
        jsonFile.write("        },\n")
    else:
        jsonFile.write("        }\n")
jsonFile.write("    },\n")
# Viết điểm
jsonFile.write("    \"Scores\" : [\n")
jsonFile.write("        {\n")
jsonFile.write("            \"user\" 	  : \"MightyDebugger\",\n")
jsonFile.write("            \"score\"   : 200\n")
jsonFile.write("		}\n")
jsonFile.write("    ]\n")
jsonFile.write("}")
jsonFile.close()
