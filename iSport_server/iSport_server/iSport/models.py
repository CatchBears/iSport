from django.db import models
from django.core.validators import MaxValueValidator, MinValueValidator

class User(models.Model):
    name = models.CharField(max_length = 100)
    password = models.CharField(max_length = 200)
    location = models.CharField(max_length = 100, blank = True)
    score = models.FloatField(validators = [MinValueValidator(0.0),
        MaxValueValidator(5.0)],  default = 5.0)
    completed_id = models.CharField(max_length = 1000, blank = True)
    uncompleted_id = models.CharField(max_length = 1000, blank = True)
    sex = models.CharField(max_length = 2, default = 'F')
    user_image = models.CharField(max_length = 1024, default = 'test.png')
    label = models.CharField(max_length = 1000, blank = True)
    img = models.ImageField(upload_to='photo',default = 'photo/user_photo.png')
    title = models.CharField(max_length = 1000, blank = True )

class Activity(models.Model):
    category = models.CharField(max_length = 20)
    begin_datatime = models.DateTimeField()
    #end_datatime = models.DateTimeFiel()
    people_count = models.IntegerField(default = 0)
    theme = models.CharField(max_length = 1000)
    location = models.CharField(max_length = 1024)
    submit_peopleId = models.IntegerField(default = 0)
    #name = models.CharField(max_length = 1024)
    joined_peopleId = models.CharField(max_length = 1000, blank = True)
    istimeout = models.CharField(blank = True, max_length = 3, default = 'n')
    details = models.CharField(max_length = 3000)
