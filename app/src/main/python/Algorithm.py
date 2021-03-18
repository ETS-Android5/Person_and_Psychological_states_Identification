from facenet_pytorch import MTCNN, InceptionResnetV1
import torch
from torchvision import datasets
from torch.utils.data import DataLoader
from PIL import Image
import numpy as np
import pyrebase

firebaseConfig = {
    'apiKey': "AIzaSyAMJv3Ooto_AAr1shf9MkPVkHbl0yqlZrM",
    'authDomain': "pythonfirebase-186f1.firebaseapp.com",
    'databaseURL': "https://pythonfirebase-186f1-default-rtdb.firebaseio.com",
    'projectId': "pythonfirebase-186f1",
    'storageBucket': "pythonfirebase-186f1.appspot.com",
    'messagingSenderId': "342993434754",
    'appId': "1:342993434754:web:64edf826b0a7062104dc9b",
    'measurementId': "G-4Y31GEGXML"
}

firebase=pyrebase.initialize_app(firebaseConfig)
db=firebase.database()

# pushing details to firebase
def push_to_firebase(emb,name,contact,medical_history,prescription_taken,additional_info):
    patient_name=name.lower()
    na=emb.detach().numpy()                        #convert tensor to numoy array
    lst=na.tolist()                                #convert numpy array to list
    data =  {  'name': patient_name,               #insrt the data you want to upload
               'contact':contact,
               'embeding':lst ,
               'medical_history': medical_history,
               'prescription_taken':prescription_taken,
               'additional_info':additional_info
               }

    patients=db.child('patients').shallow().get()     # to get primarykeys of patient table
    key=int(max(patients.val()))
    pk=key+1
    db.child("patients").child(pk).set(data)          #push data into firebase
    return "successfully updated details"

# start to pushing details to firebase
def Algorithm(path,name,contact,medical_history,prescription_taken,additional_info):
    dataset=datasets.ImageFolder(path) # photos folder path
    mtcnn = MTCNN(image_size=240, margin=0, min_face_size=20) # initializing mtcnn for face detection
    resnet = InceptionResnetV1(pretrained='vggface2').eval() # initializing resnet for face img to embeding conversion

    def collate_fn(x):
        return x[0]

    loader = DataLoader(dataset, collate_fn=collate_fn)

    for img, idx in loader:
        face, prob = mtcnn(img, return_prob=True)
        if face is not None and prob>0.90: # if face detected and porbability > 90%
            emb = resnet(face.unsqueeze(0)) # passing cropped face into resnet model to get embedding matrix
    res=push_to_firebase(emb,name,contact,medical_history,prescription_taken,additional_info)
    return res


#to get embeddings of patient
def get_embeddigns_names_from_firebsae():
    embedding_list=[]  # get all embs from firebase
    name_list=[]       # get all names frmo firebase
    patients=db.child('patients').shallow().get()
    dict_keys=list(patients.val())    #pk of table
    for i in patients.val():
        name = db.child("patients").child(i).child('name').get().val()     #get name
        e = db.child("patients").child(i).child('embeding').get().val()    #get embedding
        np_arr=np.array(e)                                                 #convert list to numpy arrat
        emb=torch.tensor(np_arr)                                           # convert np array to tensor

        name_list.append(name)
        embedding_list.append(emb)
    return embedding_list,name_list,dict_keys

#start to identify patient
def find_patient(img_paht):
    mtcnn = MTCNN(image_size=240, margin=0, min_face_size=20) # initializing mtcnn for face detection
    resnet = InceptionResnetV1(pretrained='vggface2').eval() # initializing resnet for face img to embeding conversion
    img = Image.open(img_paht)
    face, prob = mtcnn(img, return_prob=True) # returns cropped face and probability
    emb = resnet(face.unsqueeze(0)).detach() # detech is to make required gradient false
    embedding_list,name_list,dict_keys = get_embeddigns_names_from_firebsae()
    dist_list = [] # list of matched distances, minimum distance is used to identify the person
    for idx, emb_db in enumerate(embedding_list):
        dist = torch.dist(emb, emb_db.float()).item()
        dist_list.append(dist)
    idx_min = dist_list.index(min(dist_list))
    min_dist=min(dist_list)
    patient_name=name_list[idx_min]
    i=dict_keys[idx_min]
    if min_dist< 1.0:
        contact=db.child("patients").child(i).child('contact').get().val()
        medical_hist = db.child("patients").child(i).child('medical_history').get().val()
        prescription_taken=db.child("patients").child(i).child('prescription_taken').get().val()
        additional_info=db.child("patients").child(i).child('additional_info').get().val()
        s1="Patient: "+patient_name+"\n"+"EMERGENCY CONTACT: "+contact+"\n"+"MEDICAL HISTORY: "+medical_hist+"\n"+"CURRENT PRESCRIPTIONS: "+prescription_taken+"\nADDITIONAL INFO: "+"\n"+additional_info
        return s1
    else:
        return "Person detail not found"


#to edit details of a patient
def edit_details_in_firebase(name,contact,medical_history,prescription_taken,additional_info,key):
    old_contact=db.child("patients").child(key).child('contact').get().val()
    old_medical_hist = db.child("patients").child(key).child('medical_history').get().val()
    old_prescription_taken=db.child("patients").child(key).child('prescription_taken').get().val()
    old_additional_info=db.child("patients").child(key).child('additional_info').get().val()
    emb=db.child("patients").child(key).child('embeding').get().val()

    if(contact==""):
        contact=old_contact
    if(medical_history==""):
        medical_history=old_medical_hist
    if(prescription_taken==""):
        prescription_taken=old_prescription_taken
    if(additional_info==""):
        additional_info=old_additional_info

    data =  {  'name': name,               #insrt the data you want to upload
               'embeding':emb,
               'contact':contact,
               'medical_history': medical_history,
               'prescription_taken':prescription_taken,
               'additional_info':additional_info
               }

    db.child("patients").child(key).set(data)          #push data into firebase
    return "successfully updated details"

#start to edit details of patient
def edit_details(name,contact,medical_history,prescription_taken,additional_info):
    name=name.lower()
    name_list=[]
    patients=db.child('patients').shallow().get()
    dict_keys=list(patients.val())    #pk of table
    for i in dict_keys:
        name_list.append((db.child("patients").child(i).child('name').get().val()).lower())     #get names of db
    if(name in name_list):
        id_of_patient=dict_keys[name_list.index(name)]
        return edit_details_in_firebase(name,contact,medical_history,prescription_taken,additional_info,id_of_patient)
    else:
        return "patient is not registered or check patient name you have given"


